package com.example

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Doctor
import com.example.ui.MedicalViewModel
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun app_landing_screenshot() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = MedicalViewModel(application)
    val doctors = listOf(
        Doctor(
            id = 1,
            name = "أ.د. عادل منصور",
            specialty = "جراحة القلب المفتوح والأوعية الدموية",
            background = "استشاري أول جراحة القلب"
        )
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        RoleSelectionScreen(
          viewModel = viewModel,
          doctors = doctors,
          onSelectPatient = {},
          onSelectDoctor = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
