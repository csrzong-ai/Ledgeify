package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LedgerBook
import com.example.data.model.LedgerEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerDao {
    // Books Queries
    @Query("SELECT * FROM ledger_books ORDER BY id ASC")
    fun getAllBooks(): Flow<List<LedgerBook>>

    @Query("SELECT * FROM ledger_books WHERE id = :id LIMIT 1")
    fun getBookById(id: Long): Flow<LedgerBook?>

    @Query("SELECT * FROM ledger_books WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultBook(): LedgerBook?

    @Query("SELECT COUNT(*) FROM ledger_books")
    suspend fun getBookCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: LedgerBook): Long

    @Update
    suspend fun updateBook(book: LedgerBook)

    @Delete
    suspend fun deleteBook(book: LedgerBook)

    // Entries Queries
    @Query("SELECT * FROM ledger_entries WHERE bookId = :bookId ORDER BY timestamp DESC")
    fun getEntriesForBook(bookId: Long): Flow<List<LedgerEntry>>

    @Query("SELECT * FROM ledger_entries WHERE id = :id LIMIT 1")
    fun getEntryById(id: Long): Flow<LedgerEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: LedgerEntry): Long

    @Update
    suspend fun updateEntry(entry: LedgerEntry)

    @Delete
    suspend fun deleteEntry(entry: LedgerEntry)

    @Query("DELETE FROM ledger_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)

    @Query("SELECT DISTINCT partyName FROM ledger_entries WHERE bookId = :bookId AND partyName != '' ORDER BY partyName ASC")
    fun getDistinctParties(bookId: Long): Flow<List<String>>
}
