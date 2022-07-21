package io.ballerine.kmp.example.android.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.ballerine.kmp.example.android.utils.FileUriUtils

class CameraRequestActivity : AppCompatActivity() {

    companion object {
        internal const val EXTRA_ERROR = "extra.error"
        internal const val EXTRA_FILE_PATH = "extra.file_path"
        const val RESULT_ERROR = 64

        internal fun getCancelledIntent(context: Context): Intent {
            val intent = Intent()
            intent.putExtra(EXTRA_ERROR, "Task Canceled")
            return intent
        }
    }

    private var mCameraProvider: CameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadBundle(savedInstanceState)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        mCameraProvider?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun loadBundle(savedInstanceState: Bundle?) {
        mCameraProvider = CameraProvider(this)
        mCameraProvider?.onRestoreInstanceState(savedInstanceState)
        // Pick Camera Image
        savedInstanceState ?: mCameraProvider?.startIntent()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mCameraProvider?.onRequestPermissionsResult(requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCameraProvider?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        setResultCancel()
    }

    fun setImage(uri: Uri) {
        setResult(uri)
    }

    private fun setResult(uri: Uri) {
        val intent = Intent()
        intent.data = uri
        intent.putExtra(EXTRA_FILE_PATH, FileUriUtils.getRealPath(this, uri))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun setResultCancel() {
        setResult(Activity.RESULT_CANCELED, getCancelledIntent(this))
        finish()
    }

    fun setError(message: String) {
        val intent = Intent()
        intent.putExtra(EXTRA_ERROR, message)
        setResult(RESULT_ERROR, intent)
        finish()
    }
}
