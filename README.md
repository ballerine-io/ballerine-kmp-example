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
4. For taking photo create a camera image file and pass it to camera intent.
```kt
     val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
```
5. After receiving callback from Camera app, pass Uri of the camera image file to `filePathCallback`, as in example:   
```kt
    // Use Uri object instead of File to avoid storage permissions
    filePathCallback?.onReceiveValue(arrayOf(uri))
    filePathCallback = null
    ...
}
```
5. Create method that checking about finished state of the registration flow and save received results, see `checkWebViewUrl` method for more details.