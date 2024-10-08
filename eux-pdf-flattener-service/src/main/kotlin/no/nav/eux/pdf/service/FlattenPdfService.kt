package no.nav.eux.pdf.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader


@Service
class FlattenPdfService(
    val om: ObjectMapper,
) {
    val log = logger {}

    val inPath= "/tmp/pdf.js/in/"
    val outPath = "/tmp/pdf.js/out/"
    val printJsPath = "/print.js"
    val timeout = 55;
    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')


    fun flattenPdf(incomingPdf: ByteArray): ByteArray {
        val randomName = randomStringByKotlinCollectionRandom() + ".pdf"
        File(inPath + randomName).writeBytes(incomingPdf)
        val process = Runtime.getRuntime().exec(arrayOf("node", printJsPath, randomName))
        val exitCode = process.waitFor()


        if (exitCode == 0) {
            val listFiles = File(outPath).listFiles()
            listFiles.forEach { f -> log.info { "File " + f.canonicalPath } }
            val readBytes = File(outPath + randomName).readBytes()
            return readBytes
        } else {
            log.error { "Feilet å konvertere $randomName. Avsluttet med kode $exitCode" }
            try {
                BufferedReader(InputStreamReader(process.errorStream)).use { b ->
                    var line: String?
                    if ((b.readLine().also { line = it }) != null) println(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            throw IllegalArgumentException("Feilet å konvertere $randomName. Avsluttet med kode $exitCode")
        }
        // write to pdf.js/in
        // call print.js
        // read from pdf.js.out
        // delete in and out


//        val rinasak = hentRinsakNode(rinasakId)
//        val kanSlettes = kanSlettes(rinasak)
//            return incomingPdf
//        return EuxRinasakStatus(kanSlettes = kanSlettes)
/*
        val flattenerProperties: XFAFlattenerProperties = XFAFlattenerProperties()
            .setPdfVersion(XFAFlattenerProperties.PDF_1_7)
            .createXmpMetaData()
            .setTagged()
            .setMetaData(
                MetaData()
                    .setAuthor("iText Samples")
                    .setLanguage("EN")
                    .setSubject("Showing off our flattening skills")
                    .setTitle("Flattened XFA")
            )

        val xfaf: XFAFlattener = XFAFlattener()
            .setFlattenerProperties(flattenerProperties)


        xfaf.flatten(FileInputStream("xfaform.pdf"), FileOutputStream("flat.pdf"))


 */
    }

    fun randomStringByKotlinCollectionRandom() = List(16) { charPool.random() }.joinToString("")
}
