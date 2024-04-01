@file:Suppress("unused")

package com.alok.s3.presigned

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException
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
                if (!Files.exists(root)) {
                    Files.createDirectory(root)
                }
            } catch (e: Exception) {
                throw RuntimeException("Could not initialize folder for upload!")
            }
        }
    }

    fun uploadFile(file: MultipartFile) {
        try {
            extracted(file)
        } catch (e: Exception) {
            throw RuntimeException("Could not store the file. Error: " + e.message)
        }
    }

    private fun extracted(file: MultipartFile) {
        try {
            if (file.isEmpty) {
            throw RuntimeException("Failed to store empty file.")
        }
            if (file.size > 10485760) {
            throw FileSizeLimitExceededException("File size exceeds the limit of 10MB", file.size, 10485760)
        }
            val scanFile = FileScanService().scanFile(arrayOf(file))
            if (scanFile.isEmpty()) {
            log.error("Could not scan the file")
        } else {
            if (scanFile[0].detected) {
                throw RuntimeException("File is infected with virus")
            } else {
                log.info("File is clean ${file.originalFilename}")
                Files.copy(file.inputStream, root.resolve(file.originalFilename!!))
                val putObjectUsingPresignedUrl = S3ClientComponent().putObjectUsingPresignedUrl(
                    File(
                        root.resolve(file.originalFilename!!).toString()
                    )
                )
                log.info("File uploaded to S3: ${putObjectUsingPresignedUrl}")
                if (putObjectUsingPresignedUrl) {
                    log.info("File uploaded successfully to S3")
                    Files.delete(root.resolve(file.originalFilename!!))
                }
            }
        }
        } catch (e: Exception) {
            throw RuntimeException("Could not store the file. Error: " + e.message)
        }
    }

    fun loadAll(): Any {
        TODO("Not yet implemented")
    }
}