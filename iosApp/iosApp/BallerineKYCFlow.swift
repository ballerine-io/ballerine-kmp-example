//
//  BallerineKYCFlow.swift
//  iosApp
//
//  Created by Andrey on 20.07.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import UIKit
import WebKit
import shared

class BallerineKYCFlow: UIViewController {
    
    private let ballerineUrl = "https://moneco.dev.ballerine.app/?b_t=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbmRVc2VySWQiOiJhMzEyYzk1ZC03ODE4LTQyNDAtOTQ5YS1mMDRmNDEwMzRlYzEiLCJjbGllbnRJZCI6IjI2YTRmOTFiLWFhM2UtNGNlNS1hZDE1LWYzNTRiOTI1NmJmMCIsImlhdCI6MTY1OTYxNzM1NCwiZXhwIjoxNjkwMzc1NzU0LCJpc3MiOiIyNmE0ZjkxYi1hYTNlLTRjZTUtYWQxNS1mMzU0YjkyNTZiZjAifQ.Nm-j9jVh7ByHoo0WkqnIQeVR0mNWcV3TZUNknSLRtbc&b_eut=individual&b_fn=John&b_ln=Doe&b_em=test@vendy.com&b_ph=+15014384992";
    
    private let webView = WKWebView()
    
    public var onVerificaitionComplete: ((VerificationResult?) -> ())?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = .white
        view.addSubview(webView)
        
        webView.translatesAutoresizingMaskIntoConstraints = false
        webView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        webView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        webView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        webView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        
        // Add observer to listen for URL path changes
        webView.addObserver(self, forKeyPath: "URL", options: .new, context: nil)
        
        // Load ballerine webview url
        let request = URLRequest(url: URL(string: ballerineUrl)!)
        webView.load(request)
    }
    
    // Observe value
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        guard let key = change?[NSKeyValueChangeKey.newKey], let url = key as? URL else { return }
        
        if url.absoluteString.contains("final") {
            self.onVerificationCompleted(url: url)
        }
        
        if url.absoluteString.contains("close") {
            self.dismiss(animated: true)
        }
    }
    
    @objc private func onVerificationCompleted(url : URL) {
        
        var verificationResult = VerificationResult()
        
        verificationResult.isSync = getQueryStringParameter(url: url.absoluteString, param: "sync") ?? ""
        verificationResult.status = getQueryStringParameter(url: url.absoluteString, param: "status") ?? ""
        verificationResult.code = getQueryStringParameter(url: url.absoluteString, param: "code") ?? ""
        verificationResult.idvResult = getQueryStringParameter(url: url.absoluteString, param: "idvResult") ?? ""
        
        self.onVerificaitionComplete?(verificationResult)
    }
    
    private func getQueryStringParameter(url: String, param: String) -> String? {
      guard let url = URLComponents(string: url) else { return nil }
      return url.queryItems?.first(where: { $0.name == param })?.value
    }
    
}
