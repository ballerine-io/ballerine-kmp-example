## Ballerine Integration example

### Integration into Android version of KMP project

1. Generate JWT token in your backend which is required to access the Ballerine KYC flow APIs. Here is the link to the documentation on how to generate token.
2. Add `BallerineKYCFlowWebview` composable to your Activity/Fragment to initiate the web KYC verification flow process.
```kt
MainActivity.kt

    BallerineKYCFlowWebView(
           outputFileDirectory = outputFileDirectory,
           cameraExecutorService = cameraExecutorService,
           url = "$BALLERINE_WEB_URL?/b_t=$BALLERINE_API_TOKEN",
           onVerificationComplete = { verificationResult ->
                    //Do something with the verification result
    })
```
3. Once the web KYC verification flow is completed, we define the callback function `onVerificationComplete` which is passed as function-parameter in `BallerineKYCFlowWebview`.

```kt
BallerineKYCFlowWebView.kt

    onVerificationComplete(
        VerificationResult(isSync, status, idvResult, code)
    )    
```
4. Then we receive the result of the callback function `onVerificationComplete` in your Activity/Fragment.
```kt
MainActivity.kt

    onVerificationComplete = { verificationResult ->
        //TODO :: Use the verification result returned
        
        // Here we are just displaying the verification result as Text on the screen
         verificationResultText = 
             "Idv result : ${verificationResult.idvResult} \n" +
             "Status : ${verificationResult.status} \n" +
             "Code : ${verificationResult.code}"
    }
```
5. Once you have received the `VerificationResult` we can do further checks on the different values of the `VerificationResult` like `status`|`idvResult`|`code`|`isSync`.
   (As shown above in Point 4)


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