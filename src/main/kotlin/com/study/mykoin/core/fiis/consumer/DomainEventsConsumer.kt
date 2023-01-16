package com.study.mykoin.core.fiis.consumer

import com.study.mykoin.core.fiis.service.DomainEventsService
import com.study.mykoin.core.kafka.ConsumerGroupEnum
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.core.kafka.TopicEnum
import com.study.mykoin.core.kafka.startConsuming
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DomainEventsConsumer {
    private val logger = LoggerFactory.getLogger(FiiWalletConsumer::class.java)
    private lateinit var consumerJob: Deferred<Unit>
    private lateinit var updatedFiisConsumerJob: Deferred<Unit>

    @Autowired
    private lateinit var kafkaFactory: KafkaFactory

    @Autowired
    private lateinit var eventDomainService: DomainEventsService

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
    private fun startConsumer() = GlobalScope.async {
        val topic = TopicEnum.DOMAIN_EVENTS_TOPIC.topicName // It will consume from the fii_information_updated
        val consumer = kafkaFactory.getConsumer(ConsumerGroupEnum.USER_PROFILE_GROUP.groupId, listOf(topic))

        consumer.startConsuming(eventDomainService, logger)
    }
}