package io.ballerine.kmp.example.android

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
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
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService


    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

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
    fun AllowCameraAccess() {
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = getString(R.string.allow_camera_access))
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
                    CustomWebView(url = "https://vendy.dev.ballerine.app",
                        onFinish = { finalUrl ->

                            currentPage = mainScreen

                            val uri = Uri.parse(finalUrl)
                            uri.getQueryParameter("close")?.let { queryParam ->
                                secretValue = queryParam
                            }
                        })
                }
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun CustomWebView(
        url: String,
        onFinish: (String) -> Unit
    ) {

        var isOpenFrontCamera by remember {
            mutableStateOf(false)
        }
        var filePathCallback: ValueCallback<Array<Uri>>? by remember {
            mutableStateOf(null)
        }
        var showMediaPicker by remember {
            mutableStateOf(false)
        }

        if (showMediaPicker) {

            CameraView(
                isOpenFrontCamera = isOpenFrontCamera,
                outputDirectory = outputDirectory,
                executor = cameraExecutor,
                onImageCaptured = { uri ->
                    filePathCallback!!.onReceiveValue(arrayOf(uri))
                    filePathCallback = null
                    showMediaPicker = false
                }
            ) { Log.e("CameraView", "View error:", it) }
        }

        val webViewChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCb: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                if (filePathCallback != null) {
                    filePathCallback!!.onReceiveValue(null)
                }
                filePathCallback = filePathCb
                showMediaPicker = true
                return true
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.e("onConsoleMessage", " ---> ${consoleMessage?.message()}")
                return super.onConsoleMessage(consoleMessage)
            }
        }

        val webViewClient = object : WebViewClient() {

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                Log.d("doUpdateVisitedHistory", "url -> $url")

                if (url?.contains("close") == true) {
                    onFinish(url)
                }

                isOpenFrontCamera = url?.contains("selfie") == true

                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    this.webViewClient = webViewClient
                    this.webChromeClient = webViewChromeClient
                    this.settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                    }
                    loadUrl(url)
                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "Document").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}