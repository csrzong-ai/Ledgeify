package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandCyanMark
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
fun DashboardSummaryCard(
    netBalance: Double,
    totalCashIn: Double,
    totalCashOut: Double,
    cashInCount: Int,
    cashOutCount: Int,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Primary Net Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("net_balance_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(BrandBlueDark, BrandBluePrimary)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = "Wallet",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NET CASH BALANCE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f),
                                letterSpacing = 0.8.sp
                            )
                        }

                        // Status pill
                        val isPositive = netBalance >= 0
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isPositive) CashInGreen.copy(alpha = 0.25f) else CashOutRed.copy(alpha = 0.35f),
                            contentColor = Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = if (isPositive) BrandCyanMark else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPositive) "In Hand" else "Deficit",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = FormatUtils.formatCurrency(netBalance, currencySymbol),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Total Cash In minus Total Cash Out",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }

        // Split Metric Cards: Cash In (Received) and Cash Out (Paid)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cash In Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("cash_in_summary_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CashInGreenBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CashInGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Cash In",
                                tint = CashInGreenDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "$cashInCount entries",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CashInGreenDark.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Total Cash In",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = CashInGreenDark
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = FormatUtils.formatCurrency(totalCashIn, currencySymbol),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CashInGreenDark,
                        maxLines = 1
                    )
                }
            }

            // Cash Out Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .testTag("cash_out_summary_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CashOutRedBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CashOutRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Cash Out",
                                tint = CashOutRedDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "$cashOutCount entries",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CashOutRedDark.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Total Cash Out",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = CashOutRedDark
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = FormatUtils.formatCurrency(totalCashOut, currencySymbol),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CashOutRedDark,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
