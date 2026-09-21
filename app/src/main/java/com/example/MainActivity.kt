package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.LedgerViewModel
import com.example.ui.components.BrandSplashScreen
import com.example.ui.screens.LedgeifyDashboardScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: LedgerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LedgeifyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LedgeifyApp(
    viewModel: LedgerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        LedgeifyDashboardScreen(viewModel = viewModel)

        if (uiState.isSplashVisible) {
            BrandSplashScreen(
                visible = uiState.isSplashVisible,
                onDismiss = { viewModel.dismissSplash() }
            )
        }
    }
}
