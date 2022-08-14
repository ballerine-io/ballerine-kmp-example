package io.ballerine.kmp.example.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.compose.ui.platform.ComposeView
import io.ballerine.android_sdk.BallerineKYCFlowWebView
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivityXML : AppCompatActivity() {

    private lateinit var outputFileDirectory: File
    private lateinit var cameraExecutorService: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_xml)

        outputFileDirectory = getOutputDirectory()
        cameraExecutorService = Executors.newSingleThreadExecutor()

        findViewById<ComposeView>(R.id.ballerine_kyc_flow).setContent {
            BallerineKYCFlowWebView(
                outputFileDirectory = outputFileDirectory,
                cameraExecutorService = cameraExecutorService,
                url = "${MainActivity.BALLERINE_BASE_URL}?b_t=${MainActivity.BALLERINE_API_TOKEN}",
                onVerificationComplete = { verificationResult ->

                    //TODO :: Use the verification result returned

                    // Here we are just displaying the verification result as Text on the screen
                    Log.e(localClassName, "Idv result : ${verificationResult.idvResult} \n" +
                            "Status : ${verificationResult.status} \n" +
                            "Code : ${verificationResult.code}"
                    )
                })
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