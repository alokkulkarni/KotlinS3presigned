@file:Suppress("unused")

package com.alok.s3.presigned

data class FileScanResponse(
    var fileName: String,
    var detected: Boolean,
    var size: Long,
    var scanTimeMillis: Long,
    var errorMessages: String,
    val hash: String,
    var uploadTimeMillis: Long
) {
    constructor() : this("", false, 0, 0, "", "", 0)
}
