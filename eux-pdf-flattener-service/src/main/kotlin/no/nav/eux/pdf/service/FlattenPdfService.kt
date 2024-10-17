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


@Service
class FlattenPdfService(
    val om: ObjectMapper,
) {
    /*
    val options = ChromeOptions()
        .addArguments(
//            "--headless=new",
            "--headless",
            "--disable-infobars",
            "--disable-extensions",
            "--disable-popup-blocking",
//            "--remote-debugging-pipe",
            "--run-all-compositor-stages-before-draw",
            "--disable-gpu",
//        "--virtual-time-budget=10000",
            "--no-pdf-header-footer",
            "--no-sandbox",
            "--interpreter=none",
            "--disable-translate",
            "--disable-background-networking",
            "--safebrowsing-disable-auto-update",
            "--disable-sync",
            "--metrics-recording-only",
            "--disable-default-apps",
            "--no-first-run",
            "--mute-audio",
            "--hide-scrollbars",
            "--remote-debugging-port=9222",
            "--disable-crash-reporter",
            "--no-crashpad"
        )

     */
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
    val printJsPath = "/print.js"
    val countJsPath = "/count.js"
    val timeout = 55;
    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')


    fun flattenPdf(incomingPdf: ByteArray): ByteArray {
        val randomName = randomStringByKotlinCollectionRandom()
        val pdf = ".pdf"
        File(inPath + randomName + pdf).writeBytes(incomingPdf)
        val builder = ProcessBuilder().command("node", countJsPath, inPath + randomName + pdf)
        val countProcess = builder.start()
        val exitCode = countProcess.waitFor()
        if (exitCode != 0) {
            log.error { "Feilet å lese antall sider. Avsluttet med kode $exitCode" }
            try {
                val bufferedReader = BufferedReader(InputStreamReader(countProcess.errorStream))
                var line : String? = null
                do {
                    line = bufferedReader.readLine();
                    if (line != null) {
                        println(line)
                    }

                } while (line != null)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val results = BufferedReader(InputStreamReader(countProcess.inputStream)).lines().toList()
        var totalPages = 0
        if (!results.isEmpty()) {
            totalPages = results.get(0).trim().toInt()
            log.info { "Count $totalPages" }
        }
        val driver: ChromeDriver = ChromeDriver(options)
        val url = "http://localhost:8888/web/viewer.html?file=/in/$randomName$pdf#page=1"
        driver.get(url)

        for (i in 1..totalPages) {
            log.info {"Processing page $i" }
// const url = 'http://localhost:8888/web/viewer.html?file=/in/' + args[0] + args[1] + '#page=' +args[2];
            val url = "http://localhost:8888/web/viewer.html?file=/in/$randomName$pdf#page=$i"
            driver.navigate().to(url)
            //Thread.sleep(10000L)
            val wait: WebDriverWait =
                WebDriverWait(driver, Duration.ofSeconds(20))

                    /*
                    .withTimeout(Duration.ofSeconds(2))
                    .pollingEvery(Duration.ofMillis(300))
                    .ignoring(ElementNotInteractableException::class.java)
*/
/*
            wait.until {
                fun apply(driver: WebDriver): Boolean {
                    return (driver as JavascriptExecutor).executeScript("if (window.PDFViewerApplication.eventBus) { return true } else { return false } ") == "complete"
                }
            }
 */
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='textLayer']")))
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='outerContainer']")))
            log.info { "Waiting for page $i"}
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@data-page-number='$i']")))

//            val executeScript = driver.executeScript("return window.PDFViewerApplication.eventBus")
//            System.out.println("application: " + executeScript)
//            val eventBus = driver.executeScript("return window.PDFViewerApplication.eventBus")
//            System.out.println("eventbus: " + eventBus)
//            val eventBusNotNull = driver.executeScript("if (window.PDFViewerApplication.eventBus) { return true } else { return false } ")
//            System.out.println("eventbus check:  " + eventBusNotNull)
//            System.out.println("eventbus check type:  " + eventBusNotNull.javaClass.name)
            //System.out.println(driver.pageSource)
            //driver.
            //wait.until(ExpectedConditions.presenceOfElementLocated(By. .xpath(window.PDFViewerApplication.eventBus)))
            /*
            { d: WebDriver? ->
                revealed.sendKeys("Displayed")
                true
            }
                       //val mvm : MultiValueMap<String, String> = LinkedMultiValueMap(params);
             */
            //  window.PDFViewerApplication.eventBus.on('pagerendered', ...);

            log.info { "Printing page $i "}
            val output = driver.executeCdpCommand( "Page.printToPDF", printParams )
            val filename = "$outPath$randomName$i$pdf"
            val fileOutputStream: FileOutputStream = FileOutputStream(filename)
            val byteArray = Base64.getDecoder().decode(output.get("data") as String)
            fileOutputStream.write(byteArray)
            fileOutputStream.close()
            //driver.close()
        }
        driver.quit()

        log.info { "Starting merge "}
        val completeFilename = "$outPath$randomName$pdf"
//        val completeFileOutputStream: FileOutputStream = FileOutputStream(completeFilename)
//    completeFileOutputStream.write(byteArray)
//    completeFileOutputStream.close()

        val utility = PDFMergerUtility()
        utility.destinationFileName = completeFilename

        for (i in 1..totalPages) {
            val filename = "$outPath$randomName$i$pdf"
            log.info { "Adding $filename "}
            utility.addSource(filename)
        }

//    utility.addSource("testpg1.pdf")
//    utility.addSource("testpg2.pdf")
        log.info { "Finalizing merge "}
        utility.mergeDocuments(MemoryUsageSetting.setupMixed(100_000_000L))


        log.info { "Retrieving final file"}

        val readBytes = File(completeFilename).readBytes()
        return readBytes

/*
        for (i in 1..count) {
            val process = Runtime.getRuntime().exec(arrayOf("node", printJsPath, randomName, pdf, i.toString()))
            val exitCode = process.waitFor()
            try {
                val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                var line : String? = null
                do {
                    line = bufferedReader.readLine();
                    if (line != null) {
                        println(line)
                    }

                } while (line != null)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (exitCode == 0) {
                val listFiles = File(outPath).listFiles()
                listFiles.forEach { f -> log.info { "File " + f.canonicalPath } }
                val readBytes = File(outPath + randomName + pdf).readBytes()
                return readBytes
            } else {
                log.error { "Feilet å konvertere $randomName side $i. Avsluttet med kode $exitCode" }
                try {
                    val bufferedReader = BufferedReader(InputStreamReader(process.errorStream))
                    var line : String? = null
                    do {
                        line = bufferedReader.readLine();
                        if (line != null) {
                            println(line)
                        }

                    } while (line != null)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                throw IllegalArgumentException("Feilet å konvertere $randomName. Avsluttet med kode $exitCode")
            }

        }

 */
//        return ByteArray(0)


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
