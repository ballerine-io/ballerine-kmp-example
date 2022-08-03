package io.ballerine.kmp.example.android

import android.Manifest
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var outputFileDirectory: File
    private lateinit var cameraExecutorService: ExecutorService


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        outputFileDirectory = getOutputDirectory()
        cameraExecutorService = Executors.newSingleThreadExecutor()

        setContent {
            val permissionState =
                rememberMultiplePermissionsState(arrayListOf(Manifest.permission.CAMERA))

            PermissionsRequired(
                multiplePermissionsState = permissionState,
                permissionsNotGrantedContent = {
                    LaunchedEffect(key1 = "permission", block = {
                        permissionState.launchMultiplePermissionRequest()
                    })
                    AllowCameraAccess()
                },
                permissionsNotAvailableContent = {
                    AllowCameraAccess()
                },
                content = {
                    MainScreen()
                }
            )
        }
    }

    @Composable
    fun MainScreen() {

        var secretValue by remember {
            mutableStateOf("")
        }

        val noSecretString = stringResource(R.string.no_secret_available)

        LaunchedEffect(key1 = Unit, block = {
            secretValue = noSecretString
        })

        val mainScreen = 0
        val webView = 1

        var currentPage by remember {
            mutableStateOf(mainScreen)
        }

        when (currentPage) {
            mainScreen -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedButton(onClick = {
                        currentPage = webView
                    }) {
                        Text(text = stringResource(R.string.start_the_flow))
                    }

                    Text(text = secretValue)
                }
            }

            webView -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    BallerineWebView(
                        outputFileDirectory,
                        cameraExecutorService,
                        url = "https://vendy.dev.ballerine.app",
                        onVerificationComplete = { result ->
                            currentPage = mainScreen
                            secretValue = result
                        })
                }
            }
        }
    }

    @Composable
    fun AllowCameraAccess() {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = getString(R.string.allow_camera_access))
        }
    }

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