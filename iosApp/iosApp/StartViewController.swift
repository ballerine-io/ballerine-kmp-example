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
    private let label = UILabel()
    
    private let storage = BallerineStorage.shared
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = .white
        
        [label, button].forEach {
            view.addSubview($0)
            $0.translatesAutoresizingMaskIntoConstraints = false
        }
        button.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        button.centerYAnchor.constraint(equalTo: view.centerYAnchor).isActive = true
    
        button.backgroundColor = .lightGray
        button.setTitle("Start the flow", for: .normal)
        button.addTarget(self, action: #selector(startRegistrationFlow), for: .touchUpInside)
        
        label.topAnchor.constraint(equalTo: button.bottomAnchor, constant: 20).isActive = true
        label.centerXAnchor.constraint(equalTo: button.centerXAnchor).isActive = true
        
        label.font = .systemFont(ofSize: 10)
        label.textColor = .black
        setText()
    }
    
    // MARK: - Handlers
    
    @objc private func startRegistrationFlow() {
        let vc = BallerineKYCFlow { [weak self] error in
            if let err = error {
                self?.onUserRegisterError(err)
            } else {
                self?.setText()
            }
        }
        vc.modalPresentationStyle = .fullScreen
        present(vc, animated: true)
    }
    
    // MARK: - Private helpers
    
    private func setText() {
        label.text = storage.previouslyStoredKey()
    }

    private func onUserRegisterError(_ error: Error) {
        let alert = UIAlertController(title: "Error", message: error.localizedDescription, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .cancel))
        present(alert, animated: true)
    }
    
}
