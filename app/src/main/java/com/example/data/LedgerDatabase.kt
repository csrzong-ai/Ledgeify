package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.LedgerDao
import com.example.data.model.Converters
import com.example.data.model.LedgerBook
import com.example.data.model.LedgerEntry
import com.example.data.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [LedgerBook::class, LedgerEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LedgerDatabase : RoomDatabase() {

    abstract fun ledgerDao(): LedgerDao

    companion object {
        @Volatile
        private var INSTANCE: LedgerDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): LedgerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LedgerDatabase::class.java,
                    "ledgeify_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.ledgerDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: LedgerDao) {
                // Pre-populate with default cash book and initial entries
                val defaultBookId = dao.insertBook(
                    LedgerBook(
                        name = "Main Cash Book",
                        description = "Daily business & personal cash flow",
                        currencySymbol = "Rs.",
                        isDefault = true
                    )
                )

                val now = System.currentTimeMillis()
                val oneHour = 3600 * 1000L
                val oneDay = 86400 * 1000L

                dao.insertEntry(
                    LedgerEntry(
                        bookId = defaultBookId,
                        type = TransactionType.CASH_IN,
                        amount = 45000.0,
                        partyName = "Opening Cash Balance",
                        category = "Opening Balance",
                        paymentMode = "Cash",
                        notes = "Initial cash in hand for this month",
                        timestamp = now - (oneDay * 2)
                    )
                )

                dao.insertEntry(
                    LedgerEntry(
                        bookId = defaultBookId,
                        type = TransactionType.CASH_IN,
                        amount = 18500.0,
                        partyName = "Tariq Traders",
                        category = "Sales",
                        paymentMode = "Bank Transfer",
                        notes = "Invoice #1042 cleared in full",
                        timestamp = now - (oneDay + 3 * oneHour)
                    )
                )

                dao.insertEntry(
                    LedgerEntry(
                        bookId = defaultBookId,
                        type = TransactionType.CASH_OUT,
                        amount = 7200.0,
                        partyName = "Electricity & Internet Bill",
                        category = "Utility Bills",
                        paymentMode = "EasyPaisa / JazzCash",
                        notes = "Monthly office utilities payment",
                        timestamp = now - (oneDay)
                    )
                )

                dao.insertEntry(
                    LedgerEntry(
                        bookId = defaultBookId,
                        type = TransactionType.CASH_IN,
                        amount = 9500.0,
                        partyName = "Ahmad Electronics",
                        category = "Sales",
                        paymentMode = "Cash",
                        notes = "Counter retail collection",
                        timestamp = now - (2 * oneHour)
                    )
                )

                dao.insertEntry(
                    LedgerEntry(
                        bookId = defaultBookId,
                        type = TransactionType.CASH_OUT,
                        amount = 3500.0,
                        partyName = "Stationery & Supplies",
                        category = "Office Supplies",
                        paymentMode = "Cash",
                        notes = "Paper reams, ink, and daily tea expenses",
                        timestamp = now - (30 * 60 * 1000L)
                    )
                )
            }
        }
    }
}
