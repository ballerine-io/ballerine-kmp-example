## Ballerine Integration example

### Integration into Android KMP project

1. Create Fragment or Activity which contains WebView, it should load `https://2.dev.ballerine.app` URL.
2. Set webViewSettings the following WebView settings:
```kt
        webviewSettings.javaScriptEnabled = true
        webviewSettings.domStorageEnabled = true
        webviewSettings.allowFileAccess = true
```
3. Setup WebViewClient and WebChromeClient. In WebChromeClient override method `onShowFileChooser` the same way as it is implemented in `UserRegistrationFlowActivity`.
4. Add `onActivityResultListener` or `registerForActivityResult` to listen callback from the camera application: 
```kt
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
        ImagePicker.RESULT_ERROR -> {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
        else -> {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
}
```
5. Create method that checking about finished state of the registration flow and save received results, see `checkWebViewUrl` method for more details. 

