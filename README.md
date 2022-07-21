## Ballerine Integration example

### Integration into Android version of KMP project

1. Create Fragment or Activity which contains WebView
2. Set webViewSettings the following WebView settings
```kt
        webviewSettings.javaScriptEnabled = true
        webviewSettings.domStorageEnabled = true
        webviewSettings.allowFileAccess = true
```
3. Setup WebViewClient and WebChromeClient. In WebChromeClient override method `onShowFileChooser` the same way as it is implemented in `BallerineKYCFlow`.
4. Implement logic of receiving Uri for camera image, as example could be used flow implemented in `CameraRequestActivity`
5. Pass received Uri to `filePathCallback`, as in example:   
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
        ...
}
```
5. Create method that checking about finished state of the registration flow and save received results, see `checkWebViewUrl` method for more details.