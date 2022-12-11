package com.study.mykoin.crawler

import io.thelandscape.krawler.crawler.KrawlConfig
import io.thelandscape.krawler.crawler.Krawler
import io.thelandscape.krawler.http.KrawlDocument
import io.thelandscape.krawler.http.KrawlUrl
import java.time.LocalTime
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicInteger

class SimpleExample(config: KrawlConfig = KrawlConfig()) : Krawler(config) {

    private val FILTERS: Regex = Regex(".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|" +
            "mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|tar|ico))$", RegexOption.IGNORE_CASE)

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
        println("${counter.incrementAndGet()}. Crawling ${url.canonicalForm}")
        //println(doc.parsedDocument.toString())
            println(doc.parsedDocument.body().getElementById("dy-info").getElementsByClass("info").first().getElementsByClass("value").text())
        //println("containing: ${doc.parsedDocument.body().getElementsContainingOwnText("último rendimento").parents().()}")

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

fun main() {
    val config: KrawlConfig = KrawlConfig(totalPages = 3)
    val k = SimpleExample(config)

    // Add a few different hosts to the whitelist
    val allowedHosts = listOf("statusinvest.com.br")        // Website where we will fetch FIIs data
    k.whitelist.addAll(allowedHosts)

    k.start(listOf("https://statusinvest.com.br/fundos-imobiliarios/tgar11", "https://statusinvest.com.br/fundos-imobiliarios/xplg11",
        "https://statusinvest.com.br/fundos-imobiliarios/vrta11"))

}