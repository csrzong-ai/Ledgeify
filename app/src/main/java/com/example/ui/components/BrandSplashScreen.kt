package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.BrandBlueSoft
import com.example.ui.theme.BrandCyanMark
import com.example.ui.theme.BrandInkDark
import com.example.ui.theme.BrandInkMuted
import kotlinx.coroutines.delay

@Composable
fun BrandSplashScreen(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(visible) {
        if (visible) {
            // Display authentic splash brand screen for 1.6 seconds then fade to dashboard
            delay(1600)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(400)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBlueSoft)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
                .testTag("brand_splash_screen"),
            contentAlignment = Alignment.Center
        ) {
            // Center Brand Card
            Surface(
                modifier = Modifier
                    .width(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color(0x1A000000),
                        spotColor = Color(0x2A1D4ED8)
                    ),
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 36.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo Emblem
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(BrandBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        LedgeifyLogoCanvas(
                            modifier = Modifier.size(52.dp),
                            primaryColor = Color.White,
                            accentColor = BrandCyanMark
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "LEDGEIFY",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandInkDark,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "by TechStudio",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandInkMuted
                    )
                }
            }

            // Bottom Loading & Tagline
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = BrandBluePrimary,
                    strokeWidth = 2.5.dp,
                    trackColor = BrandBluePrimary.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Smart Digital Cash Book",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandInkDark
                )
            }
        }
    }
}

/**
 * Canvas drawing the exact LEDGEIFY logo from the website:
 * <path d="M13 10h8v20h16v8H13V10Z" fill="currentColor" class="text-primary-foreground"></path>
 * <path d="M25 10h12v8H25v-8Z" fill="currentColor" class="text-brand-mark"></path>
 */
@Composable
fun LedgeifyLogoCanvas(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    accentColor: Color = BrandCyanMark
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // ViewBox is 48x48
        val scaleX = w / 48f
        val scaleY = h / 48f

        // Path 1: M13 10h8v20h16v8H13V10Z
        val path1 = Path().apply {
            moveTo(13f * scaleX, 10f * scaleY)
            lineTo(21f * scaleX, 10f * scaleY)
            lineTo(21f * scaleX, 30f * scaleY)
            lineTo(37f * scaleX, 30f * scaleY)
            lineTo(37f * scaleX, 38f * scaleY)
            lineTo(13f * scaleX, 38f * scaleY)
            close()
        }
        drawPath(path = path1, color = primaryColor)

        // Path 2: M25 10h12v8H25v-8Z
        val path2 = Path().apply {
            moveTo(25f * scaleX, 10f * scaleY)
            lineTo(37f * scaleX, 10f * scaleY)
            lineTo(37f * scaleX, 18f * scaleY)
            lineTo(25f * scaleX, 18f * scaleY)
            close()
        }
        drawPath(path = path2, color = accentColor)
    }
}
