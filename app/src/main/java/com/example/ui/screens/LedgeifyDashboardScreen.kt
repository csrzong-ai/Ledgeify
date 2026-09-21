package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LedgerEntry
import com.example.data.model.TransactionType
import com.example.ui.DateFilter
import com.example.ui.FilterType
import com.example.ui.LedgerViewModel
import com.example.ui.components.AddEditEntrySheet
import com.example.ui.components.BookManagerDialog
import com.example.ui.components.CashSummaryReportDialog
import com.example.ui.components.DashboardSummaryCard
import com.example.ui.components.LedgerEntryCard
import com.example.ui.components.LedgeifyLogoCanvas
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandBlueSoft
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCyanMark
import com.example.ui.theme.BrandInkDark
import com.example.ui.theme.BrandInkMuted
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashInGreenDark
import com.example.ui.theme.CashOutRed
import com.example.ui.theme.CashOutRedDark
import com.example.ui.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgeifyDashboardScreen(
    viewModel: LedgerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currency = uiState.selectedBook?.currencySymbol ?: "Rs."

    // Group entries by date
    val groupedEntries = uiState.entries.groupBy {
        FormatUtils.formatDateGroup(it.timestamp)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandBlueSoft,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brand Icon Badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BrandBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            LedgeifyLogoCanvas(
                                modifier = Modifier.size(24.dp),
                                primaryColor = Color.White,
                                accentColor = BrandCyanMark
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "LEDGEIFY",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandInkDark,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Digital Cash Book",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = BrandInkMuted
                            )
                        }
                    }
                },
                actions = {
                    // Active Book Switcher Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.toggleBookManagerDialog(true) }
                            .testTag("book_selector_button"),
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = BrandBluePrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = uiState.selectedBook?.name ?: "Cash Book",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandInkDark,
                                maxLines = 1
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch book",
                                tint = BrandInkMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Summary / Report Button
                    IconButton(
                        onClick = { viewModel.toggleReportDialog(true) },
                        modifier = Modifier.testTag("summary_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Cash Flow Report",
                            tint = BrandBlueDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandBlueSoft
                )
            )
        },
        bottomBar = {
            // Pinned Bottom Dual Action Bar (+ Cash In & - Cash Out)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .testTag("bottom_actions_bar"),
                color = Color.White,
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // + CASH IN (Green Button)
                    Button(
                        onClick = { viewModel.openAddSheet(TransactionType.CASH_IN) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("cash_in_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CashInGreen
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+ CASH IN",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // - CASH OUT (Red Button)
                    Button(
                        onClick = { viewModel.openAddSheet(TransactionType.CASH_OUT) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("cash_out_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CashOutRed
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "- CASH OUT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Item 1: Summary Cards (Net Balance + Cash In & Cash Out)
            item(key = "summary_cards") {
                DashboardSummaryCard(
                    netBalance = uiState.netBalance,
                    totalCashIn = uiState.totalCashIn,
                    totalCashOut = uiState.totalCashOut,
                    cashInCount = uiState.cashInCount,
                    cashOutCount = uiState.cashOutCount,
                    currencySymbol = currency
                )
            }

            // Item 2: Search Input Field
            item(key = "search_bar") {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_transactions_input"),
                    placeholder = { Text("Search party, note, category, or amount...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandBluePrimary,
                        unfocusedBorderColor = BrandBorder
                    )
                )
            }

            // Item 3: Filter Chips
            item(key = "filter_row") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Type Filters
                    FilterChip(
                        selected = uiState.filterType == FilterType.ALL,
                        onClick = { viewModel.setFilterType(FilterType.ALL) },
                        label = { Text("All", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandBluePrimary,
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = uiState.filterType == FilterType.CASH_IN,
                        onClick = { viewModel.setFilterType(FilterType.CASH_IN) },
                        label = { Text("Cash In", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CashInGreen,
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = uiState.filterType == FilterType.CASH_OUT,
                        onClick = { viewModel.setFilterType(FilterType.CASH_OUT) },
                        label = { Text("Cash Out", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CashOutRed,
                            selectedLabelColor = Color.White
                        )
                    )

                    // Date Filters
                    DateFilter.entries.forEach { dFilter ->
                        val isSelected = uiState.dateFilter == dFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setDateFilter(dFilter) },
                            label = { Text(dFilter.label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF334155),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Empty State
            if (uiState.entries.isEmpty()) {
                item(key = "empty_state") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                            .testTag("empty_ledger_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(BrandBlueSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = BrandBluePrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = if (uiState.searchQuery.isNotEmpty()) "No Matching Entries" else "No Transactions Yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandInkDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (uiState.searchQuery.isNotEmpty()) {
                                    "Try adjusting your search or filter criteria"
                                } else {
                                    "Tap '+ CASH IN' or '- CASH OUT' below to record your first entry in this cash book."
                                },
                                fontSize = 12.sp,
                                color = BrandInkMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Grouped entries by date
                groupedEntries.forEach { (dateHeader, entriesForDate) ->
                    item(key = "header_$dateHeader") {
                        val inSum = entriesForDate.filter { it.type == TransactionType.CASH_IN }.sumOf { it.amount }
                        val outSum = entriesForDate.filter { it.type == TransactionType.CASH_OUT }.sumOf { it.amount }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateHeader,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (inSum > 0) {
                                    Text(
                                        text = "+ ${FormatUtils.formatCurrency(inSum, currency)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CashInGreenDark
                                    )
                                }
                                if (outSum > 0) {
                                    Text(
                                        text = "- ${FormatUtils.formatCurrency(outSum, currency)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CashOutRedDark
                                    )
                                }
                            }
                        }
                    }

                    items(entriesForDate, key = { it.id }) { entry ->
                        LedgerEntryCard(
                            entry = entry,
                            currencySymbol = currency,
                            onClick = { viewModel.openEditSheet(entry) },
                            onDelete = { viewModel.deleteEntry(entry) }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Entry Modal Bottom Sheet
    if (uiState.showAddEditSheet) {
        AddEditEntrySheet(
            bookId = uiState.selectedBookId,
            currencySymbol = currency,
            initialType = uiState.defaultSheetType,
            existingEntry = uiState.editingEntry,
            partySuggestions = uiState.partySuggestions,
            onDismiss = { viewModel.closeAddEditSheet() },
            onSave = { id, bId, type, amount, party, cat, mode, note, time ->
                viewModel.saveEntry(id, bId, type, amount, party, cat, mode, note, time)
            }
        )
    }

    // Book Manager Dialog
    if (uiState.showBookManagerDialog) {
        BookManagerDialog(
            books = uiState.books,
            selectedBookId = uiState.selectedBookId,
            onSelectBook = { viewModel.selectBook(it) },
            onCreateBook = { name, curr, desc -> viewModel.createBook(name, curr, desc) },
            onDeleteBook = { viewModel.deleteBook(it) },
            onDismiss = { viewModel.toggleBookManagerDialog(false) }
        )
    }

    // Statement Report Dialog
    if (uiState.showReportDialog) {
        CashSummaryReportDialog(
            book = uiState.selectedBook,
            entries = uiState.allEntries,
            totalCashIn = uiState.totalCashIn,
            totalCashOut = uiState.totalCashOut,
            netBalance = uiState.netBalance,
            onDismiss = { viewModel.toggleReportDialog(false) }
        )
    }
}
