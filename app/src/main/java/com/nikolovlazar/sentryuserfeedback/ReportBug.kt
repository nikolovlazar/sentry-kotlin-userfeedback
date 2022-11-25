package com.nikolovlazar.sentryuserfeedback

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.sentry.Sentry
import io.sentry.UserFeedback
import io.sentry.protocol.SentryId

@Preview
@Composable
fun ReportBug(
  navController: NavController = rememberNavController(),
  eventId: String? = ""
) {
  // create local variables for context and form fields
  val context = LocalContext.current
  var titleState by remember { mutableStateOf("") }
  var whatHappenedState by remember { mutableStateOf("") }
  var whatShouldveHappenedState by remember { mutableStateOf("") }
  var nameState by remember { mutableStateOf("") }
  var emailState by remember { mutableStateOf("") }

  // if the eventId is null, pop back to the previous screen
  if (eventId == null) {
    navController.popBackStack()
    return
  }

  val sendFeedback: () -> Unit = {
    // create a new UserFeedback instance using the eventId and form data
    val userFeedback = UserFeedback(SentryId(eventId)).apply {
      name = nameState
      email = emailState
      // concatenate some of the fields in the comments property
      comments = """Title: $titleState
==============================================
What Happened: $whatHappenedState
==============================================
What Should've Happened: $whatShouldveHappenedState"""
    }
    // send the feedback to Sentry
    Sentry.captureUserFeedback(userFeedback)

    // show a confirmational toast
    Toast.makeText(
      context,
      "Feedback sent. Thank you \uD83D\uDC96",
      Toast.LENGTH_LONG
    ).show()

    // pop back to the previous screen
    navController.popBackStack()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          IconButton(onClick = {
            navController.popBackStack()
          }) {
            Icon(
              Icons.Default.ArrowBack,
              contentDescription = "Go back"
            )
          }
        },
        title = {
          Text(text = "Report a bug")
        },
      )
    },
    content = { innerPadding ->
      Column(
        modifier = Modifier
          .padding(innerPadding)
          .padding(top = 16.dp)
          .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        CustomTextField(
          value = titleState,
          onValueChange = { value -> titleState = value },
          placeholder = "Title",
        )
        CustomTextField(
          value = whatHappenedState,
          onValueChange = { value -> whatHappenedState = value },
          placeholder = "What happened?",
          maxLines = 5,
          modifier = Modifier
            .padding(top = 16.dp)
            .height(120.dp)
        )
        CustomTextField(
          value = whatShouldveHappenedState,
          onValueChange = { value -> whatShouldveHappenedState = value },
          placeholder = "What should've happened?",
          maxLines = 5,
          modifier = Modifier
            .padding(top = 16.dp)
            .height(120.dp)
        )
        CustomTextField(
          value = nameState,
          onValueChange = { value -> nameState = value },
          placeholder = "Name",
          modifier = Modifier
            .padding(top = 16.dp)
        )
        CustomTextField(
          value = emailState,
          onValueChange = { value -> emailState = value },
          placeholder = "Email",
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
          ),
          modifier = Modifier
            .padding(top = 16.dp)
        )
        Button(
          onClick = sendFeedback,
          modifier = Modifier.padding(top = 16.dp)
        ) {
          Text("Send")
        }
      }
    }
  )
}

@Composable
fun CustomTextField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
  keyboardOptions: KeyboardOptions = KeyboardOptions(),
  maxLines: Int = 1,
) {
  TextField(
    value = value,
    onValueChange = { newValue -> onValueChange(newValue) },
    placeholder = { Text(placeholder) },
    maxLines = maxLines,
    keyboardOptions = keyboardOptions,
    shape = RoundedCornerShape(16.dp),
    colors = TextFieldDefaults.textFieldColors(
      backgroundColor = Color(0, 0, 0, 15),
      focusedIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent,
    ),
    modifier = modifier
  )
}