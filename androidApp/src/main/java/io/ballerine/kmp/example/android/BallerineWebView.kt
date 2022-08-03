package io.ballerine.kmp.example.android

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File
import java.util.concurrent.ExecutorService

/**
 * Custom Web view which handles the web verification flow
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BallerineWebView(
    outputFileDirectory: File,
    cameraExecutorService: ExecutorService,
    url: String,
    onVerificationComplete: (String) -> Unit,
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
            outputDirectory = outputFileDirectory,
            executor = cameraExecutorService,
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
            fileChooserParams: FileChooserParams?,
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

        override fun doUpdateVisitedHistory(view: WebView?, url: String, isReload: Boolean) {
            Log.d("doUpdateVisitedHistory", "url -> $url")

            val uri = Uri.parse(url)
            uri.getQueryParameter("close")?.let { paramValue ->
                if (paramValue == "true") {
                    onVerificationComplete(paramValue)
                }
            }

            isOpenFrontCamera = url.contains("selfie") == true

            super.doUpdateVisitedHistory(view, url, isReload)
        }
    }

    /**
     * Set the Display parameters to display the view in your Android app screen
     */
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
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