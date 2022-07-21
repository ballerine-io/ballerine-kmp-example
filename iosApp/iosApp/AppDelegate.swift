//
//  AppDelegate.swift
//  iosApp
//
//  Created by Andrey on 20.07.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import UIKit
import shared

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = StartViewController()
        window?.makeKeyAndVisible()
        
        return true
    }

}
