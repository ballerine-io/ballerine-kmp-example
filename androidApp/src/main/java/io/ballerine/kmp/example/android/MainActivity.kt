package io.ballerine.kmp.example.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.ballerine.android_webview.BallerineKYCFlowWebView
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {

        const val BALLERINE_WEB_URL = "https://moneco.dev.ballerine.app"

        /**
         * BALLERINE_API_TOKEN needs to be generated from the backend. Please follow the below link for more information on how to generate the tole
         * https://www.notion.so/ballerine/Ballerine-s-Developers-Documentation-c9b93462384446ef98ffb69d16865981#228240bfef6f48f3971db07ef03368c3
         */
        const val BALLERINE_API_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbmRVc2VySWQiOiJhMzEyYzk1ZC03ODE4LTQyNDAtOTQ5YS1mMDRmNDEwMzRlYzEiLCJjbGllbnRJZCI6IjI2YTRmOTFiLWFhM2UtNGNlNS1hZDE1LWYzNTRiOTI1NmJmMCIsImlhdCI6MTY1OTYxNzM1NCwiZXhwIjoxNjkwMzc1NzU0LCJpc3MiOiIyNmE0ZjkxYi1hYTNlLTRjZTUtYWQxNS1mMzU0YjkyNTZiZjAifQ.Nm-j9jVh7ByHoo0WkqnIQeVR0mNWcV3TZUNknSLRtbc"

        const val MAIN_SCREEN = 0
        const val WEB_VIEW_SCREEN = 1
    }

    private lateinit var outputFileDirectory: File
    private lateinit var cameraExecutorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        outputFileDirectory = getOutputDirectory()
        cameraExecutorService = Executors.newSingleThreadExecutor()

        setContent {
            MainScreen()
        }
    }

    // View
    @Composable
    fun MainScreen() {

        var verificationResultText by remember {
            mutableStateOf("")
        }

        val noResultDisplay = stringResource(R.string.display_verification_result)

        LaunchedEffect(key1 = Unit, block = {
            verificationResultText = noResultDisplay
        })

        var currentPage by remember {
            mutableStateOf(MAIN_SCREEN)
        }


        // Navigation handler
        when (currentPage) {

            MAIN_SCREEN -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    /**
                     * Start flow Button click listener
                     */
                    OutlinedButton(onClick = {
                        currentPage = WEB_VIEW_SCREEN
                    }) {
                        Text(text = stringResource(R.string.start_the_flow))
                    }

                    Text(text = verificationResultText)
                }
            }

            WEB_VIEW_SCREEN -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    /**
                     * Add this composable to your code to integrate the Ballerine KYC flow Web view
                     */
                    BallerineKYCFlowWebView(
                        outputFileDirectory = outputFileDirectory,
                        cameraExecutorService = cameraExecutorService,
                        url = "$BALLERINE_WEB_URL?b_t=$BALLERINE_API_TOKEN",
                        onVerificationComplete = { verificationResult ->

                            //TODO :: Use the verification result returned

                            // Exit webview and navigate to main screen
                            currentPage = MAIN_SCREEN

                            // Here we are just displaying the verification result as Text on the screen
                            verificationResultText =
                                "Idv result : ${verificationResult.idvResult} \n" +
                                        "Status : ${verificationResult.status} \n" +
                                        "Code : ${verificationResult.code}"
                        })
                }
            }
        }
    }

    // Helper functions

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "Document").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutorService.shutdown()
    }
}