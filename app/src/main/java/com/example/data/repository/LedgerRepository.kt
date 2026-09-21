package com.example.data.repository

import com.example.data.dao.LedgerDao
import com.example.data.model.LedgerBook
import com.example.data.model.LedgerEntry
import kotlinx.coroutines.flow.Flow

class LedgerRepository(private val ledgerDao: LedgerDao) {

    val allBooks: Flow<List<LedgerBook>> = ledgerDao.getAllBooks()

    fun getEntriesForBook(bookId: Long): Flow<List<LedgerEntry>> {
        return ledgerDao.getEntriesForBook(bookId)
    }

    fun getDistinctParties(bookId: Long): Flow<List<String>> {
        return ledgerDao.getDistinctParties(bookId)
    }

    suspend fun insertBook(book: LedgerBook): Long {
        return ledgerDao.insertBook(book)
    }

    suspend fun updateBook(book: LedgerBook) {
        ledgerDao.updateBook(book)
    }

    suspend fun deleteBook(book: LedgerBook) {
        ledgerDao.deleteBook(book)
    }

    suspend fun insertEntry(entry: LedgerEntry): Long {
        return ledgerDao.insertEntry(entry)
    }

    suspend fun updateEntry(entry: LedgerEntry) {
        ledgerDao.updateEntry(entry)
    }

    suspend fun deleteEntry(entry: LedgerEntry) {
        ledgerDao.deleteEntry(entry)
    }

    suspend fun deleteEntryById(id: Long) {
        ledgerDao.deleteEntryById(id)
    }

    suspend fun ensureDefaultBook(): Long {
        val existing = ledgerDao.getDefaultBook()
        if (existing != null) return existing.id

        return ledgerDao.insertBook(
            LedgerBook(
                name = "Main Cash Book",
                description = "Primary daily transactions ledger",
                currencySymbol = "Rs.",
                isDefault = true
            )
        )
    }
}
