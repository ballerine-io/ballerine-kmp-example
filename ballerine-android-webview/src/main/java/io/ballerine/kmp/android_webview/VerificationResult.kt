package io.ballerine.kmp.android_webview

import androidx.annotation.Keep

@Keep
data class VerificationResult(
    val isSync: Boolean,
    val status: String?,
    val idvResult: String?,
    val code: String?,
)
