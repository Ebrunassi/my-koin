package com.study.mykoin.core.crawler

import com.study.mykoin.core.crawler.service.KrawlerService
import io.thelandscape.krawler.crawler.KrawlConfig
import io.thelandscape.krawler.crawler.Krawler
import io.thelandscape.krawler.http.KrawlDocument
import io.thelandscape.krawler.http.KrawlUrl
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.time.LocalTime
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.scheduleAtFixedRate

@Configuration
class Krawler{

    @Autowired
    private lateinit var krawlerService: KrawlerService
    private val logger = LoggerFactory.getLogger("Krawler")
    @Value("\${krawler.delay}")
    private lateinit var delay: String
    @PostConstruct
    fun init() {
        try {
            logger.info("Starting Krawler...")
            Timer().scheduleAtFixedRate(1000, delay.toLong()) {         // Set this value in variables
                KrawlerExecuter(
                    KrawlConfig(totalPages = 6, maxDepth = 1),
                    krawlerService
                ).init()
            }
        } catch (e: Exception) {
            logger.error("Error while instantiating krawler: ${e.message}")
        }
    }

    class KrawlerExecuter(
        config: KrawlConfig = KrawlConfig(totalPages = 3, maxDepth = 1),
        val krawlerService: KrawlerService
    ) : Krawler(config) {

        private val logger = LoggerFactory.getLogger("KrawlerExecuter")
        private lateinit var consumerJob: Deferred<Unit>
        @PostConstruct
        fun init() {
            try {
                // Add a few different hosts to the whitelist
                val allowedHosts = listOf("statusinvest.com.br")        // Website where we will fetch FIIs data
                this.whitelist.addAll(allowedHosts)
                consumerJob = startKrawler()
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error(e.message)
                consumerJob.cancel(CancellationException(e.message))
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun startKrawler() = GlobalScope.async {
            println("Krawling...")
            start(
                listOf(     // TODO -- Add manually according to the already existing fiis
                    "https://statusinvest.com.br/fundos-imobiliarios/deva11",
                    "https://statusinvest.com.br/fundos-imobiliarios/irdm11",
                    "https://statusinvest.com.br/fundos-imobiliarios/mxrf11",
                    "https://statusinvest.com.br/fundos-imobiliarios/vrta11",
                    "https://statusinvest.com.br/fundos-imobiliarios/tgar11",
                    "https://statusinvest.com.br/fundos-imobiliarios/knri11"
                )
            )
        }

        private val FILTERS: Regex = Regex(
            ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|" +
                    "mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|tar|ico))$", RegexOption.IGNORE_CASE
        )

        /**
         * Threadsafe whitelist of acceptable hosts to visit
         */
        val whitelist: MutableSet<String> = ConcurrentSkipListSet()

        override fun shouldVisit(url: KrawlUrl): Boolean {
            val withoutGetParams: String = url.canonicalForm.split("?").first()
            return (!FILTERS.matches(withoutGetParams) && url.host in whitelist)
        }

        private val counter: AtomicInteger = AtomicInteger(0)

        override fun visit(url: KrawlUrl, doc: KrawlDocument) {
            try {
                println("${counter.incrementAndGet()}. Crawling ${url.canonicalForm}")
                krawlerService.handle(
                    url.path,
                    doc.parsedDocument.body()
                        ?.getElementById("dy-info")
                        ?.getElementsByClass("info")?.first()       // Get 'last income' field
                        ?.getElementsByClass("value")?.text()
                )
            } catch (e: Exception) {
                logger.error(e.message)
            }

        }

        override fun onContentFetchError(url: KrawlUrl, reason: String) {
            println("${counter.incrementAndGet()}. Tried to crawl ${url.canonicalForm} but failed to read the content.")
        }

        private var startTimestamp: Long = 0
        private var endTimestamp: Long = 0

        override fun onCrawlStart() {
            startTimestamp = LocalTime.now().toNanoOfDay()
        }

        override fun onCrawlEnd() {
            endTimestamp = LocalTime.now().toNanoOfDay()
            println("Crawled $counter pages in ${(endTimestamp - startTimestamp) / 1000000000.0} seconds.")
        }
    }
}