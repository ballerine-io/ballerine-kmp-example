## Ballerine Integration example

Requirements 

- Generate JWT token in your backend which is required to access the Ballerine KYC flow APIs. Here is the link to the documentation on how to generate token.
- Get the Ballerine webview flow service url 

### Webflow Integration steps for Android version of KMP project

Step 1. Add gradle dependency for Ballerine webview in your app-level `build.gradle` file
```kt
dependencies {
   implementation 'com.github.ballerine-io:ballerine-android-sdk:1.0.5'
}
```
   We need to add the maven dependency for jitpack in settings.gradle
```kt
allprojects {
   repositories {
      ... 
      maven("https://jitpack.io")
   }
}
```

Step 2: Declare the below required permission in your `AndroidManifest.xml` file
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
```

Step 3: Update the Ballerine service URL which is required as a parameter for `BallerineKYCFlowWebview` 
```kotlin
const val BALLERINE_WEB_URL = "$BALLERINE_BASE_URL/?b_t=$BALLERINE_API_TOKEN&b_eut=individual&b_fn=John&b_ln=Doe&b_em=test@moneco.com&b_ph=+15014384992"
```

Step 4: Add `BallerineKYCFlowWebview` composable to your Activity/Fragment to initiate the web KYC verification flow process.
   Then we receive the result of the callback function `onVerificationComplete` in your Compose Activity/Fragment.
```kotlin
BallerineKYCFlowWebView(
      outputFileDirectory = outputFileDirectory,
      cameraExecutorService = cameraExecutorService,
      url = $BALLERINE_WEB_URL,
      onVerificationComplete = { verificationResult ->
            
            //Do something with the verification result        
            
            // Here we are just displaying the verification result as a Toast message
            val toastMessage = "Idv result : ${verificationResult.idvResult} \n" +
                                    "Status : ${verificationResult.status} \n" +
                                    "Code : ${verificationResult.code}"

            // Here we are just displaying the verification result as Text on the screen
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show() 
    }
)
```
Step 5: Once the verification is complete, we receive the `verificationResult` which is a `VerificationResult` object containing detailed verification information.
   (As shown above in Point 3)


### Webflow Integration steps for iOS version of KMP project

Step 1: Add the NSCameraUsageDescription key into Info.plist file. It's needed in order to use the camera.
```xml
<key>NSCameraUsageDescription</key>
<string>Ballerine would like to access your camera in order to verify your identity.</string>
<key>NSPhotoLibraryAddUsageDescription</key>
```
Step 2: Add the `BallerineKYCViewController` and `VerficationResult` class inside the iosApp package.

Step 3: Update the Ballerine service URL inside BallerineKYCViewController which is used to load the webview
```swift
private let BALLERINE_URL = "https://example.ballerine.app/?b_t=<API_TOKEN>&b_eut=individual&b_fn=John&b_ln=Doe&b_em=test@ballerine.io&b_ph=+1100212012";
```
Step 4: Inside the ViewController we implement the following function which we can call to start our verification flow
```swift
@objc private func startVerificationFlow() {
    // Here we initialize the BallerineKYCFlow viewcontroller 
    let ballerineKycVC = BallerineKYCFlow()
        
	// Here we implement the callback function where we 
	// receive the verification result as VerificationResult object
    ballerineKycVC.onVerificaitionComplete = { [weak self] verificationResult in
        let result: [String: String] = [
            "sync" : verificationResult?.isSync ?? "",
            "status" : verificationResult?.status ?? "",
            "code": verificationResult?.code ?? "",
            "idvResult" : verificationResult?.status ?? ""
        ]
   
        DispatchQueue.main.async {
            // Here we use the verification result to display it in the view
			// Example- here we set result to a textView 
			textView.text = result.description
        }
    }
    
    ballerineKycVC.modalPresentationStyle = .fullScreen
    // Open Ballerine webview view-controller 
    present(ballerineKycVC, animated: true)
}
```
