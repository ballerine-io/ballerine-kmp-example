package io.ballerine.kmp.example

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object BallerineStorage {

    private var secret: String? = null

    fun previouslyStoredKey(): String? {
        return secret
    }

    fun saveSecret(secret: String){
        this.secret = secret
    }

}