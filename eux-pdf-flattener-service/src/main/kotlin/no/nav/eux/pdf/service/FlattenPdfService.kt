package no.nav.eux.pdf.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.apache.pdfbox.io.MemoryUsageSetting
import org.apache.pdfbox.multipdf.PDFMergerUtility
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.io.*
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit


@Service
class FlattenPdfService(
) {
    val options = ChromeOptions()
        .setExperimentalOption("debuggerAddress","127.0.0.1:9222")

    val printParams = mapOf(
        "landscape" to false,
        "paperWidth" to 8.27,
        "paperHeight" to 11.69
    )

    val log = logger {}

    val inPath= "/tmp/pdf.js/in/"
    val outPath = "/tmp/pdf.js/out/"
    val countJsPath = "/count.js"
    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val pdf = ".pdf"
    val basePdfJsUrl = "http://localhost:8888/web/viewer.html"


    fun flattenPdf(incomingPdf: ByteArray): ByteArray {
        val randomName = randomStringByKotlinCollectionRandom()
        File(inPath + randomName + pdf).writeBytes(incomingPdf)
        val builder = ProcessBuilder().command("node", countJsPath, inPath + randomName + pdf)
        val countProcess = builder.start()
        val completed = countProcess.waitFor(30L, TimeUnit.SECONDS)
        if (!completed) {
            log.error { "Timet ut å lese antall sider." }
            throw IllegalArgumentException("Timet ut å lese antall sider.")
        }
        if (countProcess.exitValue() != 0) {
            log.error { "Feilet å lese antall sider. Avsluttet med kode ${countProcess.exitValue()}" }
            try {
                val bufferedReader = BufferedReader(InputStreamReader(countProcess.errorStream))
                var line : String? = null
                do {
                    line = bufferedReader.readLine();
                    if (line != null) {
                        log.info { "Count error log:\n$line" }
                    }
                } while (line != null)
            } catch (e: IOException) {
                log.warn { "Feilet å parse error log fra count" }
            }
            throw IllegalArgumentException("Feilet å lese antall sider.")
        }
        val results = BufferedReader(InputStreamReader(countProcess.inputStream)).lines().toList()
        var totalPages = 0
        if (!results.isEmpty()) {
            totalPages = results.get(0).trim().toInt()
            log.info { "Count $totalPages" }
        }
        val driver: ChromeDriver = ChromeDriver(options)
        val url = "$basePdfJsUrl?file=/in/$randomName$pdf#page=1"
        driver.get(url)

        for (i in 1..totalPages) {
            log.info {"Processing page $i" }
            val url = "$basePdfJsUrl?file=/in/$randomName$pdf#page=$i"
            driver.navigate().to(url)
            val wait: WebDriverWait = WebDriverWait(driver, Duration.ofSeconds(20))

            log.info { "Waiting for page $i"}
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@data-page-number='$i']")))

            log.info { "Printing page $i to file"}
            val output = driver.executeCdpCommand( "Page.printToPDF", printParams )
            val filename = "$outPath$randomName$i$pdf"
            val fileOutputStream: FileOutputStream = FileOutputStream(filename)
            val byteArray = Base64.getDecoder().decode(output.get("data") as String)
            fileOutputStream.write(byteArray)
            fileOutputStream.close()
        }
        driver.quit()

        log.info { "Starting page merge"}
        val completeFilename = "$outPath$randomName$pdf"

        val utility = PDFMergerUtility()
        utility.destinationFileName = completeFilename

        for (i in 1..totalPages) {
            val filename = "$outPath$randomName$i$pdf"
            log.info { "Adding $filename to merge"}
            utility.addSource(filename)
        }

        log.info { "Finalizing merge "}
        utility.mergeDocuments(MemoryUsageSetting.setupMixed(100_000_000L))

        log.info { "Retrieving final file"}

        val readBytes = File(completeFilename).readBytes()
        return readBytes
    }

    fun randomStringByKotlinCollectionRandom() = List(16) { charPool.random() }.joinToString("")
}
