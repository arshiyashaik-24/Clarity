package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.ClarityScreen
import com.example.ui.ClarityViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ClarityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // High fidelity transitions under 300ms for a premium feel
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "MainFlowTransitions"
                    ) { screen ->
                        when (screen) {
                            ClarityScreen.LANDING -> {
                                LandingScreen(
                                    viewModel = viewModel,
                                    onNavigateToUpload = { viewModel.navigateTo(ClarityScreen.UPLOAD) },
                                    onNavigateToHistory = { viewModel.navigateTo(ClarityScreen.HISTORY) }
                                )
                            }
                            ClarityScreen.UPLOAD -> {
                                UploadScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { viewModel.navigateTo(ClarityScreen.LANDING) },
                                    onNavigateToResults = { viewModel.navigateTo(ClarityScreen.RESULTS) }
                                )
                            }
                            ClarityScreen.ANALYSIS -> {
                                AnalysisScreen(viewModel = viewModel)
                            }
                            ClarityScreen.RESULTS -> {
                                ResultsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { viewModel.navigateTo(ClarityScreen.UPLOAD) },
                                    onNavigateToHistory = { viewModel.navigateTo(ClarityScreen.HISTORY) }
                                )
                            }
                            ClarityScreen.HISTORY -> {
                                HistoryScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { viewModel.navigateTo(ClarityScreen.LANDING) },
                                    onNavigateToResults = { viewModel.navigateTo(ClarityScreen.RESULTS) }
                                )
                            }
                            ClarityScreen.DETAIL -> {
                                DetailScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { viewModel.navigateTo(ClarityScreen.RESULTS) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
