package com.study.mykoin.core.crawler.service

import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class KrawlerServiceImpl: KrawlerService{

    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage
    private val logger = LoggerFactory.getLogger("KrawlerService")
    override fun handle(url: String, data: String?) {
        println("url : $url - data : $data")
        val fiiName = url.substringAfterLast("/").uppercase()

        val fii = fiiWalletStorage.findByName(fiiName)?.let {
            it.monthlyIncome = data!!.replace(",", ".").toDouble()
            fiiWalletStorage.upsert(it)
            logger.info("'${it.name}'Updated monthly income value")
        }
    }
}

fun <R,T> logAndRun(logMessage: String,  fn: (R) -> T): (R) -> T {
    print(logMessage)
    return fn
}