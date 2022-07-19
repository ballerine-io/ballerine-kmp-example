package io.ballerine.kmp.example

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}