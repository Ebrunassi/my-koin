package com.study.mykoin.core.crawler.service

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.study.mykoin.core.crawler.model.FiiExtractedInformation
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.core.kafka.sendMessage
import com.study.mykoin.core.kafka.TopicEnum
import io.thelandscape.krawler.http.KrawlDocument
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class KrawlerServiceImpl: KrawlerService{

    @Autowired
    private lateinit var kafkaFactory: KafkaFactory

    private val logger = LoggerFactory.getLogger(KrawlerServiceImpl::class.java)
    fun String?.convertToDouble(): Double? {
        return if(this.equals("-")) 0.0
        else this?.replace(",",".")?.toDouble()
    }

    /**
     * Extract information from the body
     * TODO - For now we are just fetching lastIncome and nextIncome fields,
     * probably we will need to return more data in the future (eg.: P/VP, etc...)
     */
     fun extractInformation(name: String, data: KrawlDocument?): FiiExtractedInformation? = runBlocking {
        val extractedInformation = FiiExtractedInformation(name)

        data?.parsedDocument?.body()?.let {                 // The whole page body
            val lastIncome = launch {
                it.getElementById("dy-info")
                    ?.getElementsByClass("info")?.first()?.let {     // Block containing last income information
                        extractedInformation.lastIncome.value =
                            it.getElementsByClass("value")?.text()  // Get 'last income' value field
                            ?.convertToDouble()

                            it.getElementsByClass("sub-info")?.let { lastIncomeInfo ->
                                extractedInformation.lastIncome.yield = lastIncomeInfo[0]    // get 'yield' value field
                                    ?.getElementsByClass("sub-value")?.text()?.convertToDouble()

                                extractedInformation.lastIncome.baseValue = lastIncomeInfo[1]    // get 'yield' value field
                                    ?.getElementsByClass("sub-value")?.text()?.convertToDouble()

                                extractedInformation.lastIncome.baseDay = lastIncomeInfo[2]    // get 'yield' value field
                                    ?.getElementsByClass("sub-value")?.text()

                                extractedInformation.lastIncome.payDay = lastIncomeInfo[3]    // get 'payment day' value field
                                    ?.getElementsByClass("sub-value")?.text()
                            }
                    }
            }
            val nextIncome = launch {
                it.getElementsByClass("bg-secondary").first()?.let {     // Block containing next income information
                        extractedInformation.nextIncome.value =
                            it.getElementsByClass("value")?.text()  // Get 'last income' value field
                                ?.convertToDouble()

                        it.getElementsByClass("sub-info")?.let { lastIncomeInfo ->
                            extractedInformation.nextIncome.yield = lastIncomeInfo[0]    // get 'yield' value field
                                ?.getElementsByClass("sub-value")?.text()?.convertToDouble()

                            extractedInformation.nextIncome.baseValue = lastIncomeInfo[1]    // get 'yield' value field
                                ?.getElementsByClass("sub-value")?.text()?.convertToDouble()

                            extractedInformation.nextIncome.baseDay = lastIncomeInfo[2]    // get 'yield' value field
                                ?.getElementsByClass("sub-value")?.text()

                            extractedInformation.nextIncome.payDay = lastIncomeInfo[3]    // get 'payment day' value field
                                ?.getElementsByClass("sub-value")?.text()
                        }
                    }
            }
            lastIncome.join()
            nextIncome.join()
            return@runBlocking extractedInformation
        }
        return@runBlocking null
    }

    /**
     * Handle the extracted information preparing and modeling the model FiiExtractedInformation and
     * submit it to FII_WALLET_TOPIC for those who are interested in receive this information
     */
    override fun handle(url: String, data: KrawlDocument?) {

        val fiiName = url.substringAfterLast("/").uppercase()
        val extractedInformation = extractInformation(fiiName, data)

        kafkaFactory.getProducer().sendMessage(
            TopicEnum.FIIS_WALLET_TOPIC.topicName,
            fiiName,
            jsonMapper().writeValueAsString(extractedInformation),
            logger
        ).also {
            logger.info("Message sent successfully!")
        }
    }
}

fun <R,T> logAndRun(logMessage: String,  fn: (R) -> T): (R) -> T {
    print(logMessage)
    return fn
}