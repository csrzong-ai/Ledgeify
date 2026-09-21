package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class TransactionType {
    CASH_IN,
    CASH_OUT
}

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return try {
            TransactionType.valueOf(value)
        } catch (e: Exception) {
            TransactionType.CASH_IN
        }
    }
}

@Entity(tableName = "ledger_books")
data class LedgerBook(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val currencySymbol: String = "Rs.",
    val createdAt: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false
)

@Entity(
    tableName = "ledger_entries",
    foreignKeys = [
        ForeignKey(
            entity = LedgerBook::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["bookId"]),
        Index(value = ["timestamp"])
    ]
)
data class LedgerEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val type: TransactionType,
    val amount: Double,
    val partyName: String,
    val category: String = "General",
    val paymentMode: String = "Cash",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
