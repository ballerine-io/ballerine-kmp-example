//
//  StartViewController.swift
//  iosApp
//
//  Created by Andrey on 20.07.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import UIKit
import shared

class StartViewController: UIViewController {
    
    private let button = UIButton()
    private let textView = UILabel()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = .white
        
        [textView, button].forEach {
            view.addSubview($0)
            $0.translatesAutoresizingMaskIntoConstraints = false
        }
        button.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        button.centerYAnchor.constraint(equalTo: view.centerYAnchor).isActive = true
        
        button.backgroundColor = .systemBlue
        button.setTitle("Start Verification flow", for: .normal)
        button.addTarget(self, action: #selector(startVerificationFlow), for: .touchUpInside)
        
        textView.topAnchor.constraint(equalTo: button.bottomAnchor, constant: 20).isActive = true
        textView.centerXAnchor.constraint(equalTo: button.centerXAnchor).isActive = true
        textView.textColor = .black
    }
    
    // MARK: - Handlers
    
    @objc private func startVerificationFlow() {
        
        let ballerineKycVC = BallerineKYCViewController()
        
        ballerineKycVC.onVerificaitionComplete = { [weak self] verificationResult in
            let data: [String: String] = [
                "sync" : verificationResult?.isSync ?? "",
                "status" : verificationResult?.status ?? "",
                "code": verificationResult?.code ?? "",
                "idvResult" : verificationResult?.status ?? ""
            ]
            
            print("Verification result : \(data)")
            
            DispatchQueue.main.async {
                self?.setResult(result: data)
            }
        }
    
        ballerineKycVC.modalPresentationStyle = .fullScreen
        
        present(ballerineKycVC, animated: true)
    }
    
    // MARK: - Private helpers
    
    // Here we display the verification result in our view
    private func setResult(result: [String:String]) {
        textView.text = result.description
    }
}
