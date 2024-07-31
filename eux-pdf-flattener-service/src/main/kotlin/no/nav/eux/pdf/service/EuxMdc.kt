package no.nav.eux.pdf.service

import org.slf4j.MDC

fun <T> T.mdc(
    rinasakId: Int? = null,
): T {
    "rinasakId" leggTil rinasakId
    return this
}

private infix fun String.leggTil(value: Any?) {
    if (value != null) MDC.put(this, "$value")
}
