package io.ballerine.kmp.example.android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import io.ballerine.kmp.example.BallerineStorage
import io.ballerine.kmp.example.android.camera.CameraRequestActivity
import io.ballerine.kmp.example.android.camera.CameraRequestActivity.Companion.RESULT_ERROR
import kotlin.random.Random


class BallerineKYCFlow : AppCompatActivity() {

    lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registration_flow)

        webView = findViewById(R.id.web_view)

        webView.webViewClient = object : WebViewClient() {}

        setWebViewSettings(webView.settings)
        webView.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                Log.d("TAG", "Permission Request")
                if (this@BallerineKYCFlow.filePathCallback != null) {
                    this@BallerineKYCFlow.filePathCallback!!.onReceiveValue(null)
                }
                this@BallerineKYCFlow.filePathCallback = filePathCallback
                startCameraActivity()
                return true
            }
        }

        loadUrl()
        checkWebViewUrl()
    }

    private fun startCameraActivity() {
        startForProfileImageResult
            .launch(Intent(this, CameraRequestActivity::class.java))
    }

    private fun checkWebViewUrl(){
        Handler(Looper.getMainLooper()).postDelayed({
            if(webView.url?.contains("final") == true){
                val uri = Uri.parse(webView.url)
                BallerineStorage.saveSecret(uri.query ?: "")
                setResult(Activity.RESULT_OK)
                finish()
                return@postDelayed
            }
            checkWebViewUrl()
        }, 2000)
    }

    private fun setWebViewSettings(webviewSettings: WebSettings) {
        webviewSettings.javaScriptEnabled = true
        webviewSettings.domStorageEnabled = true
        webviewSettings.allowFileAccess = true
    }


    private fun loadUrl() {
        webView.loadUrl("https://2.dev.ballerine.app")
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val uri: Uri = data?.data!!

                    // Use Uri object instead of File to avoid storage permissions
                    filePathCallback!!.onReceiveValue(arrayOf(uri))
                    filePathCallback = null
                }
                RESULT_ERROR -> {
                    Toast.makeText(this, "Loading Image Error", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
}
