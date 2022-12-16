package com.study.mykoin.core.fiis.consumer

import com.study.mykoin.core.fiis.service.FiiWalletService
import com.study.mykoin.core.kafka.ConsumerGroupEnum
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.core.kafka.TopicEnum
import com.study.mykoin.core.kafka.startConsuming
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

/**
 * This consumer consumes information from KrawlerService
 * It is data that was got from Krawler and built using the extracted information,
 * the consumed information will be used to update 'fiis' collection
 */
@Configuration
class FiiWalletConsumer {
    private val logger = LoggerFactory.getLogger(FiiWalletConsumer::class.java)
    private lateinit var consumerJob: Deferred<Unit>

    @Autowired
    private lateinit var kafkaFactory: KafkaFactory
    @Autowired
    private lateinit var fiiWalletService: FiiWalletService

    @PostConstruct
    fun init() {
        try {
            consumerJob = startConsumer()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error(e.message)
            consumerJob.cancel(CancellationException(e.message))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun startConsumer() = GlobalScope.async {
        val topic = TopicEnum.FIIS_WALLET_TOPIC.topicName
        val consumer = kafkaFactory.getConsumer(ConsumerGroupEnum.FII_WALLET_GROUP.groupId, listOf(topic))

        consumer.startConsuming(fiiWalletService)
    }
}