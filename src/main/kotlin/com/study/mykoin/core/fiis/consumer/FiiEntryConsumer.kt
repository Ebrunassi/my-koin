package com.study.mykoin.core.fiis.consumer

import com.study.mykoin.core.fiis.service.FiiEntryService
import com.study.mykoin.core.kafka.ConsumerGroupEnum
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.core.kafka.TopicEnum
import com.study.mykoin.core.kafka.startConsuming
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.util.*

/**
 * This consumer consumes information from FiiController
 * that is, new FIIs that was bought and submited via api
 */
@Configuration
class FiiEntryConsumer {
    private val logger = LoggerFactory.getLogger(FiiEntryConsumer::class.java)
    private lateinit var consumerJob: Deferred<Unit>

    @Autowired
    private lateinit var kafkaFactory: KafkaFactory
    @Autowired
    private lateinit var fiiEntryService: FiiEntryService

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
        val topic = TopicEnum.FIIS_HISTORY_TOPIC.topicName
        val consumer = kafkaFactory.getConsumer(ConsumerGroupEnum.FII_ENTRY_GROUP.groupId, listOf(topic))

        /**
         * runBlocking is for you to block the main thread.
         * coroutineScope is for you to block the runBlocking.
         */
        // runBlocking {     // Is that necessary?
        // coroutineScope {      // We don't have any suspend methods inside of it
        consumer.startConsuming(fiiEntryService, logger)
        // }
        // }
    }
}
