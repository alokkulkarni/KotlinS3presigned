package com.alok.s3.presigned

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class PresignedApplication {


	@Bean
	fun run() = CommandLineRunner {
//		val generatePresignedPutUrl = S3ClientComponent().generatePresignedPutUrl("documents-s3-presigned", "test.txt")
//		println("Presigned URL: ${generatePresignedPutUrl.url().toExternalForm()}")
		StorageService.init()
	}
}

fun main(args: Array<String>) {
	runApplication<PresignedApplication>(*args)
}