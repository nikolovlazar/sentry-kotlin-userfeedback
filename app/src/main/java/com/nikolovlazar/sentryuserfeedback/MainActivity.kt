package com.nikolovlazar.sentryuserfeedback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nikolovlazar.sentryuserfeedback.ui.theme.SentryUserFeedbackTheme
import io.sentry.Sentry
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }
            SentryUserFeedbackTheme {
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        Scaffold(
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            content = { innerPadding ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    color = MaterialTheme.colors.background
                                ) {
                                    TriggerError(snackbarHostState, navController)
                                }
                            }
                        )
                    }
                    composable("reportBug/{eventId}") { backStackEntry ->
                        ReportBug(
                            snackbarHostState,
                            navController,
                            backStackEntry.arguments?.getString("eventId")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TriggerError(snackbarHostState: SnackbarHostState, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val onClick: () -> Unit = {
        try {
            // faulty method
            throw Exception("CRASH")
        } catch (e: Exception) {
            val eventId = Sentry.captureException(e)
            coroutineScope.launch {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = "Oh no \uD83D\uDE27",
                    actionLabel = "Report this!"
                )
                when (snackbarResult) {
                    SnackbarResult.Dismissed -> {}
                    SnackbarResult.ActionPerformed -> {
                        // Navigate to new screen
                        navController.navigate("reportBug/$eventId")
                    }
                }
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(onClick = onClick) {
            Text("Unleash chaos!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SentryUserFeedbackTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val navController = rememberNavController()
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            content = { innerPadding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    color = MaterialTheme.colors.background
                ) {
                    TriggerError(snackbarHostState, navController)
                }
            }
        )
    }
}