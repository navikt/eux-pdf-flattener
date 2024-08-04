package no.nav.eux.pdf.webapp

import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap

fun <T> T.toOkResponseEntity() = ResponseEntity<T>(this, OK)

fun <T> T.toPdfOkResponseEntity(map: MultiValueMap<String, String>) = ResponseEntity<T>(this, map, OK)

fun <T> T.toCreatedEmptyResponseEntity() = ResponseEntity<T>(this, CREATED)

fun <T> T.toCreatedResponseEntity() = ResponseEntity<T>(this, CREATED)

fun <T> T.toNoContentResponseEntity() = ResponseEntity<T>(this, NO_CONTENT)

fun <T> T.toNotFoundResponseEntity() = ResponseEntity<T>(this, NOT_FOUND)

fun Any.toEmptyResponseEntity() = ResponseEntity.noContent().build<Unit>()
