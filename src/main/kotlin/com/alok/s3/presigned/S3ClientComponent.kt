@file:Suppress("unused")

package com.alok.s3.presigned

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

@Component
class S3ClientComponent {

    companion object {

        val log: Logger = LoggerFactory.getLogger(S3ClientComponent::class.java)

        private const val S3_BUCKET_NAME = "documents-s3-presigned"
        private const val S3_BUCKET_REGION = "eu-west-2"
        private const val S3_BUCKET_URL = "https://documents-s3-presigned.s3.eu-west-2.amazonaws.com"
        private const val S3_BUCKET_KEY = "documents-alok"
        private const val S3_BUCKET_SECRET = "documents-s3-presigned"
        private const val S3_BUCKET_ENDPOINT = "https://s3.eu-west-2.amazonaws.com"
        private const val S3_BUCKET_PATH = "documents-s3-presigned"
        private val restTemplate = RestTemplate().apply {
            messageConverters = listOf(MappingJackson2HttpMessageConverter())
        }

    }

    fun generatePresignedPutUrl(bucketName: String, bucketKey: String): PresignedPutObjectRequest {
        try {
            val signer = S3Presigner.builder()
                .region(software.amazon.awssdk.regions.Region.EU_WEST_2)
                .build()
            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(bucketKey)
                .build()
            val putObjectPresignedUrl = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(24))
                .putObjectRequest(putObjectRequest)
                .build()

            val presignedRequest = signer.presignPutObject(putObjectPresignedUrl)
            val url = presignedRequest.url()
            log.info("Generated presigned Put url: $url")
            return presignedRequest
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun putObjectUsingPresignedUrl(fileToPut: File) {
        try {
           val preSignedUrl = generatePresignedPutUrl(S3_BUCKET_NAME, "${S3_BUCKET_KEY}/${fileToPut.name}")
           val url =  URL(preSignedUrl.url().toExternalForm())

           val request = HttpRequest.newBuilder()

           val httpClient = HttpClient.newHttpClient()
           val response = httpClient.send(request.uri(url.toURI())
                        .PUT(HttpRequest.BodyPublishers.ofFile(fileToPut.toPath()))
                        .build(),
                HttpResponse.BodyHandlers.ofString())

            log.info("Response from PUT request: ${response.body()} : ${response.statusCode()}")

        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }


}