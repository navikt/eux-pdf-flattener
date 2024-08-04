package no.nav.eux.pdf.webapp

import no.nav.eux.pdf.openapi.api.PdfApi
import no.nav.eux.pdf.service.FlattenPdfService
import no.nav.security.token.support.core.api.Protected
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RestController

@RestController
class PdfApiImpl(
    val pdfService: FlattenPdfService
) : PdfApi {

    @Protected
    override fun flattenPdf(file: Resource?): ResponseEntity<Resource> {
        val byteArrayResource = ByteArrayResource(pdfService.flattenPdf(file!!.contentAsByteArray))
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
        map.add(HttpHeaders.CONTENT_LENGTH, byteArrayResource.contentLength().toString())
        map.add(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(file!!.filename).build().toString())

        return byteArrayResource.toPdfOkResponseEntity(map)
    }
}
