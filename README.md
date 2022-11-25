# User Feedback in Android (Kotlin)

This repo is an implementation of Sentry's [User Feedback API](https://docs.sentry.io/platforms/android/enriching-events/user-feedback/) in a sample Android (Kotlin) app.

## Running it locally
1. After you've cloned the repo, head to `app/src/main` and rename the `AndroidManifest.sample.xml` to `AndroidManifest.xml`
2. Open the new `AndroidManifest.xml` file and replace the `"[DSN_STRING]"` on line 33 with your own DSN string.
   - To obtain a DSN string, create a Sentry account at [sentry.io/signup](https://sentry.io/signup/) if you haven't already
   - Create a new Android project
   - Scroll down to the "Connecting the SDK to Sentry" section, you should see your project's DSN in the second line of the code block
