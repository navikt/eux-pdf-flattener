package no.nav.eux.pdf.webapp

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ResponseExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [IllegalArgumentException::class])
    fun handleResponseStatusException(illegalArgumentException: IllegalArgumentException, request: WebRequest): ResponseEntity<Any> {
        return super.handleExceptionInternal(illegalArgumentException, illegalArgumentException.message, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request)!!
    }
}