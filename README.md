## Ballerine KMP Integration example

### Integration into Android

1. Create a Fragment or Activity which contains the WebView, it should load `https://[YOUR-SUBDOMAIN].dev.ballerine.app` (sanbox) or `https://[YOUR-SUBDOMAIN].ballerine.app` (prod) URL.
2. Set webViewSettings to the following WebView settings:
```kt
        webviewSettings.javaScriptEnabled = true
        webviewSettings.domStorageEnabled = true
        webviewSettings.allowFileAccess = true
```
3. Setup the WebViewClient and WebChromeClient. In WebChromeClient override the method `onShowFileChooser` the same way as it is implemented in `UserRegistrationFlowActivity`.
4. Add `onActivityResultListener` or `registerForActivityResult` to listen to the callback from the camera application: 
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
5. Create a method that is checking for the finished state of the registration flow and saves the received results, see `checkWebViewUrl` method for more details. 


### Integration into iOS

1. Add the NSCameraUsageDescription key into Info.plist file. It's needed in order to use the camera.
2. Create UIViewController which contains WKWebView, it should load `https://[YOUR-SUBDOMAIN].dev.ballerine.app` (sanbox) or `https://[YOUR-SUBDOMAIN].ballerine.app` (prod) URL.
3. Add web view key-value observer to detect URL updates:
```swift
webView.addObserver(self, forKeyPath: "URL", options: .new, context: nil)
```
4. Implement observeValue forKeyPath method to handle URL updates: 
```swift
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        guard let key = change?[NSKeyValueChangeKey.newKey], let url = (key as? NSURL)?.absoluteString else { return }
        if url.absoluteString.contains("final") {
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) { [weak self] in
                self?.finishWithSecret(url.query ?? "")
            }
        }
    }
```
5. Create a method that is checking for the finished state of the registration flow and saves the received results, see `finishWithSecret` method for more details.
