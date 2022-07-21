package io.ballerine.kmp.example

import kotlin.native.concurrent.ThreadLocal


@ThreadLocal
object BallerineStorage {

    private var secretKey: String? = null

    fun previouslyStoredKey(): String? {
        return secretKey
    }

    fun saveSecret(secret: String){
        this.secretKey = secret
    }

}