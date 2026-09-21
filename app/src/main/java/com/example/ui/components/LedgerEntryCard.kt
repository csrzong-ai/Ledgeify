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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LedgerEntry
import com.example.data.model.TransactionType
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

@Composable
fun LedgerEntryCard(
    entry: LedgerEntry,
    currencySymbol: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCashIn = entry.type == TransactionType.CASH_IN
    val primaryColor = if (isCashIn) CashInGreen else CashOutRed
    val bgColor = if (isCashIn) CashInGreenBg else CashOutRedBg

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("ledger_entry_card_${entry.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Direction Icon Indicator
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCashIn) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = if (isCashIn) "Cash In" else "Cash Out",
                    tint = primaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details (Party, Category, Mode, Note, Time)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entry.partyName.ifEmpty { if (isCashIn) "Cash In" else "Cash Out" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandInkDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Category Chip
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = entry.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Payment Mode Chip
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE2E8F0).copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = entry.paymentMode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (entry.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = entry.notes,
                        fontSize = 12.sp,
                        color = BrandInkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = FormatUtils.formatTime(entry.timestamp),
                    fontSize = 11.sp,
                    color = BrandInkMuted.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount & Actions
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val prefix = if (isCashIn) "+" else "-"
                Text(
                    text = "$prefix ${FormatUtils.formatCurrency(entry.amount, currencySymbol)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("edit_entry_button_${entry.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_entry_button_${entry.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
