package com.study.mykoin.usecases.consumer

import com.study.mykoin.usecases.kafka.ConsumerGroupEnum
import com.study.mykoin.usecases.kafka.KafkaFactory
import com.study.mykoin.usecases.kafka.TopicEnum
import com.study.mykoin.usecases.kafka.startConsuming
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

// TODO - Not used at the moment
@Component
class ProfileConsumer {
    private val logger = LoggerFactory.getLogger(FiiWalletConsumer::class.java)
    private lateinit var consumerJob: Deferred<Unit>
    private lateinit var updatedFiisConsumerJob: Deferred<Unit>

    @Autowired
    private lateinit var kafkaFactory: KafkaFactory

    //@Autowired
    //private lateinit var profileService: ProfileService

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
        val topic = TopicEnum.USER_PROFILE_TOPIC.topicName // It will consume from the profile_topic
        val consumer = kafkaFactory.getConsumer(ConsumerGroupEnum.USER_PROFILE_GROUP.groupId, listOf(topic))

        //consumer.startConsuming(profileService, logger)
    }
}
