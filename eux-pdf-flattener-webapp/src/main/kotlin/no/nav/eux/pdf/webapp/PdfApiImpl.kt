package no.nav.eux.pdf.webapp

import no.nav.eux.pdf.openapi.api.PdfApi
import no.nav.eux.pdf.service.FlattenPdfService
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PdfApiImpl(
    val pdfService: FlattenPdfService
) : PdfApi {

    override fun flattenPdf(file: Resource?): ResponseEntity<Resource> {
        return ByteArrayResource(pdfService.flattenPdf(file!!.contentAsByteArray)).toOkResponseEntity()
    }
}
