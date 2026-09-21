package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LedgerBook
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandBlueSoft
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandInkDark
import com.example.ui.theme.BrandInkMuted
import com.example.ui.theme.CashOutRed

@Composable
fun BookManagerDialog(
    books: List<LedgerBook>,
    selectedBookId: Long,
    onSelectBook: (Long) -> Unit,
    onCreateBook: (name: String, currency: String, desc: String) -> Unit,
    onDeleteBook: (LedgerBook) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAddingNewBook by remember { mutableStateOf(false) }
    var newBookName by remember { mutableStateOf("") }
    var newBookCurrency by remember { mutableStateOf("Rs.") }
    var newBookDesc by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("book_manager_dialog"),
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAddingNewBook) "Create Cash Book" else "My Cash Books",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandInkDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = BrandInkMuted
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isAddingNewBook) {
                    Text(
                        text = "Switch between different business or personal cash accounts.",
                        fontSize = 13.sp,
                        color = BrandInkMuted
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(books, key = { it.id }) { book ->
                            val isSelected = book.id == selectedBookId
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectBook(book.id)
                                        onDismiss()
                                    }
                                    .testTag("book_item_${book.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) BrandBlueSoft else Color(0xFFF8FAFC)
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, BrandBluePrimary) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) BrandBluePrimary else Color(0xFFE2E8F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Book,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else Color(0xFF475569),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = book.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandInkDark
                                        )
                                        Text(
                                            text = "Currency: ${book.currencySymbol}",
                                            fontSize = 11.sp,
                                            color = BrandInkMuted
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = BrandBluePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else if (books.size > 1) {
                                        IconButton(
                                            onClick = { onDeleteBook(book) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Book",
                                                tint = Color(0xFF94A3B8),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { isAddingNewBook = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("add_new_book_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add New Cash Book", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    // Create Form
                    Text(
                        text = "Cash Book Name *",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandInkDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newBookName,
                        onValueChange = {
                            newBookName = it
                            errorMessage = null
                        },
                        placeholder = { Text("e.g. Shop Ledger, Personal Expenses") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_book_name_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Currency Symbol",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandInkDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val popularCurrencies = listOf("Rs.", "$", "AED", "€", "₹", "£")
                        popularCurrencies.forEach { symbol ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { newBookCurrency = symbol },
                                color = if (newBookCurrency == symbol) BrandBluePrimary else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = symbol,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (newBookCurrency == symbol) Color.White else Color(0xFF334155),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Description (Optional)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandInkDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newBookDesc,
                        onValueChange = { newBookDesc = it },
                        placeholder = { Text("Short description...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 2
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage!!,
                            color = CashOutRed,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isAddingNewBook = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Back")
                        }
                        Button(
                            onClick = {
                                if (newBookName.trim().isEmpty()) {
                                    errorMessage = "Please enter a book name"
                                    return@Button
                                }
                                onCreateBook(newBookName, newBookCurrency, newBookDesc)
                            },
                            modifier = Modifier
                                .weight(1.4f)
                                .testTag("confirm_create_book_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                        ) {
                            Text("Create Book")
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
