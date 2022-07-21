package io.ballerine.kmp.example.android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import io.ballerine.kmp.example.BallerineStorage
import io.ballerine.kmp.example.android.utils.FileUtil
import io.ballerine.kmp.example.android.utils.PermissionUtil
import java.io.File


class BallerineKYCFlow : AppCompatActivity() {

    lateinit var webView: WebView
    companion object{
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
        private const val CAMERA_INTENT_REQ_CODE = 4281
        private const val PERMISSION_INTENT_REQ_CODE = 4282
    }
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraFile: File? = null

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
                checkPermission()
                return true
            }
        }

        loadUrl()
        checkWebViewUrl()
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


    private fun checkPermission() {
        if (isPermissionGranted(this)) {
            // Permission Granted, Start Camera Intent
            startCameraIntent()
        } else {
            // Request Permission
            requestPermission()
        }
    }

    private fun startCameraIntent() {
        // Create and get empty file to store capture image content
        val file = FileUtil.getImageFile(fileDir = getExternalFilesDir(Environment.DIRECTORY_DCIM) ?: filesDir)
        mCameraFile = file

        // Check if file exists
        if (file != null && file.exists()) {
            val cameraIntent = getCameraIntent(this, file)
            startActivityForResult(cameraIntent, CAMERA_INTENT_REQ_CODE)
        }
    }

    private fun getCameraIntent(context: Context, file: File): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority =
                context.packageName + ".imagepicker.provider"
            val photoURI = FileProvider.getUriForFile(context, authority, file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
        }

        return intent
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            getRequiredPermission(this),
            PERMISSION_INTENT_REQ_CODE
        )
    }

    private fun isPermissionGranted(context: Context): Boolean {
        return getRequiredPermission(context).none {
            !PermissionUtil.isPermissionGranted(context, it)
        }
    }

    private fun getRequiredPermission(context: Context): Array<String> {
        return REQUIRED_PERMISSIONS.filter {
            PermissionUtil.isPermissionInManifest(context, it)
        }.toTypedArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_INTENT_REQ_CODE) {
            // Check again if permission is granted
            if (isPermissionGranted(this)) {
                // Permission is granted, Start Camera Intent
                startCameraIntent()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_INTENT_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                filePathCallback?.onReceiveValue(arrayOf(Uri.fromFile(mCameraFile)))
                filePathCallback = null
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
