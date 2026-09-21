package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LedgerEntry
import com.example.data.model.TransactionType
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandInkDark
import com.example.ui.theme.BrandInkMuted
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashInGreenBg
import com.example.ui.theme.CashInGreenDark
import com.example.ui.theme.CashOutRed
import com.example.ui.theme.CashOutRedBg
import com.example.ui.theme.CashOutRedDark
import com.example.ui.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntrySheet(
    bookId: Long,
    currencySymbol: String,
    initialType: TransactionType,
    existingEntry: LedgerEntry?,
    partySuggestions: List<String>,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        bookId: Long,
        type: TransactionType,
        amount: Double,
        partyName: String,
        category: String,
        paymentMode: String,
        notes: String,
        timestamp: Long
    ) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var selectedType by remember {
        mutableStateOf(existingEntry?.type ?: initialType)
    }

    var amountText by remember {
        mutableStateOf(if (existingEntry != null) "%.2f".format(existingEntry.amount) else "")
    }

    var partyName by remember {
        mutableStateOf(existingEntry?.partyName ?: "")
    }

    val cashInCategories = remember {
        listOf("Sales", "Payment Received", "Loan Received", "Salary", "Investment", "Commission", "Personal", "Other")
    }
    val cashOutCategories = remember {
        listOf("Purchase / Stock", "Supplier", "Utility Bills", "Rent", "Salaries", "Groceries", "Transport", "Maintenance", "Personal", "Other")
    }

    val currentCategories = if (selectedType == TransactionType.CASH_IN) cashInCategories else cashOutCategories

    var selectedCategory by remember {
        mutableStateOf(existingEntry?.category ?: currentCategories.first())
    }

    val paymentModes = remember {
        listOf("Cash", "Bank Transfer", "EasyPaisa / JazzCash", "Debit / Credit Card", "Online", "Cheque")
    }

    var selectedPaymentMode by remember {
        mutableStateOf(existingEntry?.paymentMode ?: paymentModes.first())
    }

    var notes by remember {
        mutableStateOf(existingEntry?.notes ?: "")
    }

    var timestamp by remember {
        mutableStateOf(existingEntry?.timestamp ?: System.currentTimeMillis())
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    val isCashIn = selectedType == TransactionType.CASH_IN
    val primaryColor = if (isCashIn) CashInGreen else CashOutRed
    val primaryBg = if (isCashIn) CashInGreenBg else CashOutRedBg

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingEntry == null) "New Ledger Entry" else "Edit Ledger Entry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandInkDark
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_entry_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = BrandInkMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Segmented Tab for CASH IN vs CASH OUT
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Cash In Tab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            selectedType = TransactionType.CASH_IN
                            if (!cashInCategories.contains(selectedCategory)) {
                                selectedCategory = cashInCategories.first()
                            }
                        }
                        .testTag("tab_cash_in"),
                    color = if (isCashIn) CashInGreen else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isCashIn) Color.White else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ CASH IN (Received)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCashIn) Color.White else Color(0xFF64748B)
                        )
                    }
                }

                // Cash Out Tab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            selectedType = TransactionType.CASH_OUT
                            if (!cashOutCategories.contains(selectedCategory)) {
                                selectedCategory = cashOutCategories.first()
                            }
                        }
                        .testTag("tab_cash_out"),
                    color = if (!isCashIn) CashOutRed else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (!isCashIn) Color.White else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "- CASH OUT (Paid)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isCashIn) Color.White else Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Amount Input Field
            Text(
                text = "Amount *",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandInkDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_amount_input"),
                placeholder = { Text("0.00", fontSize = 24.sp, color = Color(0xFF94A3B8)) },
                leadingIcon = {
                    Text(
                        text = currencySymbol,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = BrandBorder
                )
            )

            // Quick Add Shortcut Chips
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chips = listOf(500, 1000, 5000, 10000, 25000)
                chips.forEach { chipVal ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                val currentVal = amountText.toDoubleOrNull() ?: 0.0
                                val newVal = currentVal + chipVal
                                amountText = "%.2f".format(newVal)
                            },
                        color = primaryBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "+$chipVal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Party / Customer / Supplier Name
            Text(
                text = if (isCashIn) "Received From (Party / Customer Name)" else "Paid To (Party / Supplier / Expense Name)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandInkDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = partyName,
                onValueChange = { partyName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_party_input"),
                placeholder = { Text(if (isCashIn) "e.g. Customer Ali, Cash Counter" else "e.g. Supplier Imran, Electric Bill") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF64748B)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBluePrimary,
                    unfocusedBorderColor = BrandBorder
                )
            )

            // Party Quick Suggestions if available
            if (partySuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    partySuggestions.take(5).forEach { suggestion ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { partyName = suggestion },
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 11.sp,
                                color = Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selection
            Text(
                text = "Category",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandInkDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentCategories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Mode
            Text(
                text = "Payment Mode",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandInkDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                paymentModes.forEach { mode ->
                    val isSelected = mode == selectedPaymentMode
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPaymentMode = mode },
                        label = { Text(mode, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandBluePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date & Time Display
            Text(
                text = "Date & Time",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandInkDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        // Resets to now if tapped
                        timestamp = System.currentTimeMillis()
                    },
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Date",
                        tint = BrandBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = FormatUtils.formatFullDateTime(timestamp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Set to Now",
                        fontSize = 11.sp,
                        color = BrandBluePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes / Remarks
            Text(
                text = "Notes / Description (Optional)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandInkDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_notes_input"),
                placeholder = { Text("Add transaction details, invoice #, or remarks...") },
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBluePrimary,
                    unfocusedBorderColor = BrandBorder
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = CashOutRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Entry Button
            Button(
                onClick = {
                    val parsedAmount = amountText.toDoubleOrNull()
                    if (parsedAmount == null || parsedAmount <= 0.0) {
                        errorMessage = "Please enter a valid amount greater than 0"
                        return@Button
                    }

                    val finalParty = partyName.trim().ifEmpty {
                        if (isCashIn) "Cash In" else "Cash Out"
                    }

                    onSave(
                        existingEntry?.id ?: 0L,
                        bookId,
                        selectedType,
                        parsedAmount,
                        finalParty,
                        selectedCategory,
                        selectedPaymentMode,
                        notes,
                        timestamp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_entry_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                )
            ) {
                Text(
                    text = if (existingEntry == null) {
                        if (isCashIn) "+ SAVE CASH IN" else "- SAVE CASH OUT"
                    } else "UPDATE ENTRY",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
