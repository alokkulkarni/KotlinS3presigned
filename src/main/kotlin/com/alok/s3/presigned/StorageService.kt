@file:Suppress("unused")

package com.alok.s3.presigned

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


@Service
class StorageService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(StorageService::class.java)

        val root: Path = Paths.get("uploads")

        fun init() {
            try {
                Files.createDirectory(root)
            } catch (e: Exception) {
                throw RuntimeException("Could not initialize folder for upload!")
            }
        }
    }

    fun uploadFile(file: MultipartFile) {
        try {
            Files.copy(file.inputStream, root.resolve(file.originalFilename!!))
            S3ClientComponent().putObjectUsingPresignedUrl(File(root.resolve(file.originalFilename!!).toString()))
            Files.delete(root.resolve(file.originalFilename!!))
        } catch (e: Exception) {
            throw RuntimeException("Could not store the file. Error: " + e.message)
        }
    }

    fun loadAll(): Any {
        TODO("Not yet implemented")
    }
}