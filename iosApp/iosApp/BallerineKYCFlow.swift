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
    
    private let completion: (Error?) -> Void
    
    init(completion: @escaping (Error?) -> Void) {
        self.completion = completion
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = .white
        view.addSubview(webView)
        
        webView.translatesAutoresizingMaskIntoConstraints = false
        webView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        webView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        webView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        webView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        
        webView.addObserver(self, forKeyPath: "URL", options: .new, context: nil)
        loadUrl()
    }
    
    // Observe value
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        guard let key = change?[NSKeyValueChangeKey.newKey], let url = key as? URL else { return }
        if url.absoluteString.contains("final") {
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) { [weak self] in
                self?.finishWithSecret(url.query ?? "")
            }
        }
    }
    
    private func loadUrl() {
        let request = URLRequest(url: URL(string: ballerineUrl)!)
        webView.load(request)
    }
    
    private func finishWithSecret(_ secret: String) {
        completion(nil)
        dismiss(animated: true)
    }
    
}
