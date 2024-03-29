package com.study.mykoin.core.crawler

import com.study.mykoin.core.crawler.service.KrawlerService
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.domain.fiis.Fii
import io.thelandscape.krawler.crawler.KrawlConfig
import io.thelandscape.krawler.crawler.Krawler
import io.thelandscape.krawler.http.KrawlDocument
import io.thelandscape.krawler.http.KrawlUrl
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.apache.kafka.clients.producer.KafkaProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.time.LocalTime
import java.util.Timer
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.scheduleAtFixedRate

@Configuration
class Krawler {

    @Autowired
    private lateinit var krawlerService: KrawlerService
    @Autowired
    private lateinit var kafkaFactory: KafkaFactory
    @Autowired
    private lateinit var walletStorage: FiiWalletStorage
    private val logger = LoggerFactory.getLogger("Krawler")
    @Value("\${krawler.delay}")
    private lateinit var delay: String
    @Value("\${krawler.host.visit.template}")
    private lateinit var visitHostTemplate: String

    private val walletSet = mutableSetOf<Fii>()

    companion object {
        const val FIIS_WALLET_TOPIC = "fiis_wallet"
    }

    @PostConstruct
    fun init() {
        try {
            walletStorage.findByUserId(1.toLong())?.let { walletSet.addAll(it) } // TODO - When adding login system, get the user id of the logged user
            logger.info("Starting Krawler...")
            Timer().scheduleAtFixedRate(5000, delay.toLong()) { // Set this value in variables
                KrawlerExecuter(
                    KrawlConfig(totalPages = 20, maxDepth = 1),
                    krawlerService,
                    kafkaFactory.getProducer()
                ).init()
            }
        } catch (e: Exception) {
            logger.error("Error while instantiating krawler: ${e.message}")
        }
    }

    inner class KrawlerExecuter(
        config: KrawlConfig = KrawlConfig(totalPages = 3, maxDepth = 1),
        val krawlerService: KrawlerService,
        val kafkaProducer: KafkaProducer<String, String>
    ) : Krawler(config) {

        private val logger = LoggerFactory.getLogger("KrawlerExecuter")
        private lateinit var consumerJob: Deferred<Unit>
        private val FILTERS: Regex = Regex(
            ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|" +
                "mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|tar|ico))$",
            RegexOption.IGNORE_CASE
        )
        private var startTimestamp: Long = 0
        private var endTimestamp: Long = 0
        @PostConstruct
        fun init() {
            try {
                // Add a few different hosts to the whitelist
                val allowedHosts = listOf("statusinvest.com.br") // Website where we will fetch FIIs data
                this.whitelist.addAll(allowedHosts)
                consumerJob = startKrawler()
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error(e.message)
                consumerJob.cancel(CancellationException(e.message))
            }
        }

        override fun visit(url: KrawlUrl, doc: KrawlDocument) {
            try {
                logger.info("${counter.incrementAndGet()}. Crawling ${url.canonicalForm}")
                krawlerService.handle(url.path, doc)
            } catch (e: Exception) {
                logger.error(e.message)
                throw e
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun startKrawler() = GlobalScope.async {
            walletSet.map {
                visitHostTemplate + it.name
            }.let { start(it) }
        }

        /**
         * Threadsafe whitelist of acceptable hosts to visit
         */
        val whitelist: MutableSet<String> = ConcurrentSkipListSet()

        override fun shouldVisit(url: KrawlUrl): Boolean {
            val withoutGetParams: String = url.canonicalForm.split("?").first()
            return (!FILTERS.matches(withoutGetParams) && url.host in whitelist)
        }

        private val counter: AtomicInteger = AtomicInteger(0)
        override fun onContentFetchError(url: KrawlUrl, reason: String) {
            println("${counter.incrementAndGet()}. Tried to crawl ${url.canonicalForm} but failed to read the content.")
        }

        override fun onCrawlStart() {
            startTimestamp = LocalTime.now().toNanoOfDay()
        }

        override fun onCrawlEnd() {
            endTimestamp = LocalTime.now().toNanoOfDay()
            println("Crawled $counter pages in ${(endTimestamp - startTimestamp) / 1000000000.0} seconds.")
        }
    }
}
