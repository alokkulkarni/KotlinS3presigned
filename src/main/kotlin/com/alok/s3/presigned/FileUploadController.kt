package com.alok.s3.presigned

import org.apache.tomcat.util.http.fileupload.FileUploadException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@RestController
@RequestMapping("/api")
class FileUploadController(val storageService: StorageService) {



    @PostMapping("/files/upload")
    fun uploadFile(@RequestParam("files") files: Array<MultipartFile>): ResponseEntity<List<String>> {
        val fileNames = ArrayList<String>()
        for (file in files) {
            storageService.uploadFile(file)
            fileNames.add(file.originalFilename!!)
        }
        return ResponseEntity.ok().body(fileNames)
    }
}

@ControllerAdvice
class FileUploadExceptionAdvice : ResponseEntityExceptionHandler() {
//
//    @ExceptionHandler(FileUploadException::class)
//    fun handleUploadFileException(exc: FileUploadException): ResponseEntity<Any> {
//        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("One or more files couldn't be uploaded! ${exc.localizedMessage}")
//    }

//    @ExceptionHandler(MaxUploadSizeExceededException::class)
//    fun handleMaxFileSizeException(exc: MaxUploadSizeExceededException): ResponseEntity<Any> {
//        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body( "One or more files are too large! ${exc.localizedMessage}")
//    }

}