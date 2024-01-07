@file:Suppress("unused")

package com.alok.s3.presigned

import fi.solita.clamav.ClamAVClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*
import java.util.stream.Collectors


@Service
class FileScanService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(FileScanService::class.java)
    }


    fun scanFile(files: Array<MultipartFile>) : List<FileScanResponse> {
        val clamAVClient = ClamAVClient("scan", 3310)
        if(!clamAVClient.ping()) {
            logger.error("ClamAV is not running")
            return emptyList()
        }
        return Arrays.stream(files).map { file ->
            val fileScanResponse = FileScanResponse()
            val startTime = System.currentTimeMillis()
            fileScanResponse.uploadTimeMillis = startTime
            try {
                val response = clamAVClient.scan(file.inputStream)
                val status = ClamAVClient.isCleanReply(response)
                fileScanResponse.detected = !status
                logger.info("File ${file.originalFilename} is ${if (status) "clean" else "infected"}")
            } catch (e: Exception) {
                logger.error("Error while scanning file ${file.originalFilename}", e)
                fileScanResponse.errorMessages = e.localizedMessage
            }
            fileScanResponse.fileName = file.originalFilename!!
            fileScanResponse.size = file.size
            fileScanResponse.scanTimeMillis = System.currentTimeMillis() - startTime
            return@map fileScanResponse
        }.collect(Collectors.toList())
    }
}