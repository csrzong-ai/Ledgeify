package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LedgerDatabase
import com.example.data.model.LedgerBook
import com.example.data.model.LedgerEntry
import com.example.data.model.TransactionType
import com.example.data.repository.LedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class FilterType {
    ALL,
    CASH_IN,
    CASH_OUT
}

enum class DateFilter(val label: String) {
    ALL_TIME("All Time"),
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month")
}

data class DashboardUiState(
    val books: List<LedgerBook> = emptyList(),
    val selectedBookId: Long = 0L,
    val selectedBook: LedgerBook? = null,
    val entries: List<LedgerEntry> = emptyList(),
    val allEntries: List<LedgerEntry> = emptyList(),
    val partySuggestions: List<String> = emptyList(),
    val totalCashIn: Double = 0.0,
    val totalCashOut: Double = 0.0,
    val netBalance: Double = 0.0,
    val cashInCount: Int = 0,
    val cashOutCount: Int = 0,
    val searchQuery: String = "",
    val filterType: FilterType = FilterType.ALL,
    val dateFilter: DateFilter = DateFilter.ALL_TIME,
    val isSplashVisible: Boolean = true,
    val showAddEditSheet: Boolean = false,
    val editingEntry: LedgerEntry? = null,
    val defaultSheetType: TransactionType = TransactionType.CASH_IN,
    val showBookManagerDialog: Boolean = false,
    val showReportDialog: Boolean = false
)

class LedgerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LedgerRepository

    private val _selectedBookId = MutableStateFlow(0L)
    private val _searchQuery = MutableStateFlow("")
    private val _filterType = MutableStateFlow(FilterType.ALL)
    private val _dateFilter = MutableStateFlow(DateFilter.ALL_TIME)
    private val _isSplashVisible = MutableStateFlow(true)
    private val _showAddEditSheet = MutableStateFlow(false)
    private val _editingEntry = MutableStateFlow<LedgerEntry?>(null)
    private val _defaultSheetType = MutableStateFlow(TransactionType.CASH_IN)
    private val _showBookManagerDialog = MutableStateFlow(false)
    private val _showReportDialog = MutableStateFlow(false)

    private val _entriesForCurrentBook = MutableStateFlow<List<LedgerEntry>>(emptyList())
    private val _partiesForCurrentBook = MutableStateFlow<List<String>>(emptyList())

    val books: StateFlow<List<LedgerBook>>

    val uiState: StateFlow<DashboardUiState>

    init {
        val database = LedgerDatabase.getDatabase(application, viewModelScope)
        repository = LedgerRepository(database.ledgerDao())

        books = repository.allBooks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Observe books to initialize selectedBookId if needed
        viewModelScope.launch {
            books.collect { bookList ->
                if (bookList.isNotEmpty()) {
                    if (_selectedBookId.value == 0L || bookList.none { it.id == _selectedBookId.value }) {
                        val defaultBook = bookList.find { it.isDefault } ?: bookList.first()
                        _selectedBookId.value = defaultBook.id
                    }
                } else {
                    val defaultId = repository.ensureDefaultBook()
                    _selectedBookId.value = defaultId
                }
            }
        }

        // Whenever selectedBookId changes, collect its entries & parties
        viewModelScope.launch {
            _selectedBookId.collect { bookId ->
                if (bookId > 0L) {
                    launch {
                        repository.getEntriesForBook(bookId).collect { entries ->
                            _entriesForCurrentBook.value = entries
                        }
                    }
                    launch {
                        repository.getDistinctParties(bookId).collect { parties ->
                            _partiesForCurrentBook.value = parties
                        }
                    }
                }
            }
        }

        // Combine everything into uiState
        uiState = combine(
            books,
            _selectedBookId,
            _entriesForCurrentBook,
            _partiesForCurrentBook,
            _searchQuery,
            _filterType,
            _dateFilter,
            _isSplashVisible,
            _showAddEditSheet,
            _editingEntry,
            _defaultSheetType,
            _showBookManagerDialog,
            _showReportDialog
        ) { args ->
            @Suppress("UNCHECKED_CAST")
            val bList = args[0] as List<LedgerBook>
            val selId = args[1] as Long
            @Suppress("UNCHECKED_CAST")
            val allBookEntries = args[2] as List<LedgerEntry>
            @Suppress("UNCHECKED_CAST")
            val parties = args[3] as List<String>
            val query = args[4] as String
            val fType = args[5] as FilterType
            val dFilter = args[6] as DateFilter
            val splash = args[7] as Boolean
            val addSheet = args[8] as Boolean
            @Suppress("UNCHECKED_CAST")
            val editEntry = args[9] as LedgerEntry?
            val sheetType = args[10] as TransactionType
            val bookMgr = args[11] as Boolean
            val report = args[12] as Boolean

            val selectedBook = bList.find { it.id == selId }

            // Calculate totals for active book (unfiltered by search for accurate header summary)
            var cashInSum = 0.0
            var cashOutSum = 0.0
            var inCount = 0
            var outCount = 0

            for (entry in allBookEntries) {
                if (entry.type == TransactionType.CASH_IN) {
                    cashInSum += entry.amount
                    inCount++
                } else {
                    cashOutSum += entry.amount
                    outCount++
                }
            }

            val net = cashInSum - cashOutSum

            // Filter entries by query, type, and date
            val filtered = allBookEntries.filter { entry ->
                // Type Filter
                val matchesType = when (fType) {
                    FilterType.ALL -> true
                    FilterType.CASH_IN -> entry.type == TransactionType.CASH_IN
                    FilterType.CASH_OUT -> entry.type == TransactionType.CASH_OUT
                }

                // Date Filter
                val matchesDate = matchesDateFilter(entry.timestamp, dFilter)

                // Search Query
                val matchesQuery = if (query.isBlank()) {
                    true
                } else {
                    val q = query.trim().lowercase()
                    entry.partyName.lowercase().contains(q) ||
                    entry.category.lowercase().contains(q) ||
                    entry.notes.lowercase().contains(q) ||
                    entry.paymentMode.lowercase().contains(q) ||
                    entry.amount.toString().contains(q)
                }

                matchesType && matchesDate && matchesQuery
            }

            DashboardUiState(
                books = bList,
                selectedBookId = selId,
                selectedBook = selectedBook,
                entries = filtered,
                allEntries = allBookEntries,
                partySuggestions = parties,
                totalCashIn = cashInSum,
                totalCashOut = cashOutSum,
                netBalance = net,
                cashInCount = inCount,
                cashOutCount = outCount,
                searchQuery = query,
                filterType = fType,
                dateFilter = dFilter,
                isSplashVisible = splash,
                showAddEditSheet = addSheet,
                editingEntry = editEntry,
                defaultSheetType = sheetType,
                showBookManagerDialog = bookMgr,
                showReportDialog = report
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState()
        )
    }

    private fun matchesDateFilter(timestamp: Long, filter: DateFilter): Boolean {
        if (filter == DateFilter.ALL_TIME) return true

        val entryCal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val nowCal = Calendar.getInstance()

        return when (filter) {
            DateFilter.TODAY -> {
                entryCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                entryCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)
            }
            DateFilter.THIS_WEEK -> {
                entryCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                entryCal.get(Calendar.WEEK_OF_YEAR) == nowCal.get(Calendar.WEEK_OF_YEAR)
            }
            DateFilter.THIS_MONTH -> {
                entryCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                entryCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)
            }
            DateFilter.ALL_TIME -> true
        }
    }

    fun dismissSplash() {
        _isSplashVisible.value = false
    }

    fun selectBook(id: Long) {
        _selectedBookId.value = id
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: FilterType) {
        _filterType.value = type
    }

    fun setDateFilter(filter: DateFilter) {
        _dateFilter.value = filter
    }

    fun openAddSheet(type: TransactionType) {
        _editingEntry.value = null
        _defaultSheetType.value = type
        _showAddEditSheet.value = true
    }

    fun openEditSheet(entry: LedgerEntry) {
        _editingEntry.value = entry
        _defaultSheetType.value = entry.type
        _showAddEditSheet.value = true
    }

    fun closeAddEditSheet() {
        _showAddEditSheet.value = false
        _editingEntry.value = null
    }

    fun toggleBookManagerDialog(show: Boolean) {
        _showBookManagerDialog.value = show
    }

    fun toggleReportDialog(show: Boolean) {
        _showReportDialog.value = show
    }

    fun saveEntry(
        id: Long = 0L,
        bookId: Long,
        type: TransactionType,
        amount: Double,
        partyName: String,
        category: String,
        paymentMode: String,
        notes: String,
        timestamp: Long
    ) {
        viewModelScope.launch {
            val entry = LedgerEntry(
                id = id,
                bookId = bookId,
                type = type,
                amount = amount,
                partyName = partyName.trim(),
                category = category.trim().ifEmpty { "General" },
                paymentMode = paymentMode.trim().ifEmpty { "Cash" },
                notes = notes.trim(),
                timestamp = timestamp
            )
            if (id == 0L) {
                repository.insertEntry(entry)
            } else {
                repository.updateEntry(entry)
            }
            closeAddEditSheet()
        }
    }

    fun deleteEntry(entry: LedgerEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
            if (_editingEntry.value?.id == entry.id) {
                closeAddEditSheet()
            }
        }
    }

    fun createBook(name: String, currency: String = "Rs.", description: String = "") {
        viewModelScope.launch {
            val newId = repository.insertBook(
                LedgerBook(
                    name = name.trim().ifEmpty { "New Cash Book" },
                    description = description.trim(),
                    currencySymbol = currency.trim().ifEmpty { "Rs." },
                    isDefault = false
                )
            )
            _selectedBookId.value = newId
            _showBookManagerDialog.value = false
        }
    }

    fun deleteBook(book: LedgerBook) {
        viewModelScope.launch {
            val currentBooks = books.value
            if (currentBooks.size > 1) {
                repository.deleteBook(book)
                val remaining = currentBooks.filter { it.id != book.id }
                if (remaining.isNotEmpty()) {
                    _selectedBookId.value = remaining.first().id
                }
            }
        }
    }
}
