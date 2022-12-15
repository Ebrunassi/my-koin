package com.study.mykoin.core.fiis.kafka.consumer

import com.study.mykoin.core.fiis.http.controller.FiiController
import com.study.mykoin.core.fiis.kafka.config.KafkaFactory
import com.study.mykoin.core.fiis.service.EntryService
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.*

@Configuration
class FiiConsumer{
    private val logger = LoggerFactory.getLogger("FiiConsumer")
    private lateinit var consumerJob: Deferred<Unit>

    @Autowired
    private lateinit var kafkaFactory: KafkaFactory
    @Autowired
    private lateinit var entryService: EntryService

    companion object {
        val consumerGroup = "FII_ENTRY_CONSUMER"
    }

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
        val topic = FiiController.FIIS_TOPIC
        val consumer = kafkaFactory.getConsumer(consumerGroup, listOf(topic))
        var totalCount = 0L

        runBlocking {
            coroutineScope {
                consumer.use {
                    while (true) {
                        totalCount = consumer
                            .poll(Duration.ofMillis(1000))
                            .fold(totalCount) { accumulator, record ->
                                val newCount = accumulator + 1
                                println("Consumed record with key ${record.key()} and value ${record.value()}, and updated total count to $newCount")
                                entryService.handler(record.value())
                                newCount
                            }
                    }
                }
            }
        }
    }
}