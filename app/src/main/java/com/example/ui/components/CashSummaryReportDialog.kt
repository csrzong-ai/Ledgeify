package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LedgerBook
import com.example.data.model.LedgerEntry
import com.example.data.model.TransactionType
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandInkDark
import com.example.ui.theme.BrandInkMuted
import com.example.ui.theme.CashInGreen
import com.example.ui.theme.CashInGreenBg
import com.example.ui.theme.CashInGreenDark
import com.example.ui.theme.CashOutRed
import com.example.ui.theme.CashOutRedBg
import com.example.ui.theme.CashOutRedDark
import com.example.ui.util.FormatUtils

@Composable
fun CashSummaryReportDialog(
    book: LedgerBook?,
    entries: List<LedgerEntry>,
    totalCashIn: Double,
    totalCashOut: Double,
    netBalance: Double,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currency = book?.currencySymbol ?: "Rs."

    // Group entries by category
    val cashInByCategory = entries
        .filter { it.type == TransactionType.CASH_IN }
        .groupBy { it.category }
        .mapValues { it.value.sumOf { entry -> entry.amount } }

    val cashOutByCategory = entries
        .filter { it.type == TransactionType.CASH_OUT }
        .groupBy { it.category }
        .mapValues { it.value.sumOf { entry -> entry.amount } }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("cash_summary_report_dialog"),
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEBF1FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = BrandBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Cash Flow Statement",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandInkDark
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = BrandInkMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Summary report for ${book?.name ?: "Cash Book"}",
                    fontSize = 12.sp,
                    color = BrandInkMuted
                )

                // Financial Overview Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Cash In", fontSize = 13.sp, color = CashInGreenDark, fontWeight = FontWeight.Medium)
                            Text(FormatUtils.formatCurrency(totalCashIn, currency), fontSize = 13.sp, color = CashInGreen, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Cash Out", fontSize = 13.sp, color = CashOutRedDark, fontWeight = FontWeight.Medium)
                            Text(FormatUtils.formatCurrency(totalCashOut, currency), fontSize = 13.sp, color = CashOutRed, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Net Cash Balance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandInkDark)
                            Text(
                                text = FormatUtils.formatCurrency(netBalance, currency),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (netBalance >= 0) CashInGreen else CashOutRed
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Entries Count", fontSize = 12.sp, color = BrandInkMuted)
                            Text("${entries.size} transactions", fontSize = 12.sp, color = BrandInkDark, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Category Breakdown Cash In
                if (cashInByCategory.isNotEmpty()) {
                    Text(
                        text = "Cash In by Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CashInGreenDark
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        cashInByCategory.forEach { (cat, amount) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(cat, fontSize = 12.sp, color = Color(0xFF334155))
                                Text(FormatUtils.formatCurrency(amount, currency), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CashInGreen)
                            }
                        }
                    }
                }

                // Category Breakdown Cash Out
                if (cashOutByCategory.isNotEmpty()) {
                    Text(
                        text = "Cash Out by Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CashOutRedDark
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        cashOutByCategory.forEach { (cat, amount) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(cat, fontSize = 12.sp, color = Color(0xFF334155))
                                Text(FormatUtils.formatCurrency(amount, currency), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CashOutRed)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Copy / Share Statement Button
                Button(
                    onClick = {
                        val statementText = buildString {
                            appendLine("=== LEDGEIFY CASH FLOW STATEMENT ===")
                            appendLine("Book: ${book?.name ?: "Main Cash Book"}")
                            appendLine("Date Generated: ${FormatUtils.formatDate(System.currentTimeMillis())}")
                            appendLine("------------------------------------")
                            appendLine("Total Cash In:  ${FormatUtils.formatCurrency(totalCashIn, currency)}")
                            appendLine("Total Cash Out: ${FormatUtils.formatCurrency(totalCashOut, currency)}")
                            appendLine("Net Balance:    ${FormatUtils.formatCurrency(netBalance, currency)}")
                            appendLine("Total Entries:  ${entries.size}")
                            appendLine("====================================")
                        }

                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Ledgeify Statement", statementText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Statement copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("copy_statement_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy Statement Summary", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {}
    )
}
