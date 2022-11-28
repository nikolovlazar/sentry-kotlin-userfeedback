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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      // create the navController and snackbarHostState
      val navController = rememberNavController()
      val snackbarHostState = remember { SnackbarHostState() }
      SentryUserFeedbackTheme {
        // add a NavHost as the root component and assign the navController
        NavHost(navController = navController, startDestination = "main") {
          // create a route for the main screen
          composable("main") {
            // wrap the main component with a Scaffold and assign the snackbarHost
            // the Scaffold is required in order to display the Snackbar
            Scaffold(
              snackbarHost = { SnackbarHost(snackbarHostState) },
              content = { innerPadding ->
                Surface(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                  color = MaterialTheme.colors.background
                ) {
                  // render the main screen component
                  // and pass the snackbarHostState and navController
                  TriggerError(snackbarHostState, navController)
                }
              }
            )
          }
          // create a route for the Report a Bug screen
          // the User Feedback API requires the eventId of the last reported exception
          // so we'll add the eventId as part of the route
          composable("reportBug/{eventId}") { backStackEntry ->
            // render the ReportBug component
            // and pass the navController and the eventId from the args
            ReportBug(
              navController,
              backStackEntry.arguments?.getString("eventId")
            )
          }
        }
      }
    }
  }
}

// define the main screen component
@Composable
fun TriggerError(
  snackbarHostState: SnackbarHostState,
  navController: NavController
) {
  // to display the snackbar in a method we need to create a coroutine scope
  val coroutineScope = rememberCoroutineScope()
  val onClick: () -> Unit = {
    try {
      // faulty method
      throw Exception("CRASH")
    } catch (e: Exception) {
      // report the exception to Sentry and obtain the eventId
      // TODO: Report exception to Sentry and obtain eventId
      coroutineScope.launch {
        // launch the snackbar
        val snackbarResult = snackbarHostState.showSnackbar(
          message = "Oh no \uD83D\uDE27",
          actionLabel = "Report this!"
        )
        when (snackbarResult) {
          SnackbarResult.Dismissed -> {}
          SnackbarResult.ActionPerformed -> {
            // navigate to the Report a Bug screen
            // use the exception's eventId as part of the route
            // TODO: Navigate to ReportBug screen with the eventId included
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