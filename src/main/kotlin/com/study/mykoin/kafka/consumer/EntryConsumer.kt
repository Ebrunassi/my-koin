package com.study.mykoin.kafka.consumer

import com.study.mykoin.api.http.controller.FiiController
import com.study.mykoin.kafka.config.KafkaFactory
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class EntryConsumer {

    @Autowired
    private var kafkaFactory: KafkaFactory? = null
    private val log : Logger = LoggerFactory.getLogger("EntryConsumer")


    fun init(kafkaFactory: KafkaFactory) {
        log.info("Entry consumer initiated")
        this.kafkaFactory = kafkaFactory
    }

    /*
    fun startKafkaConsumer() {
        val consumer = kafkaFactory!!.getConsumer("fiis-group-id")
        consumer.subscribe(setOf(FiiController.FIIS_TOPIC))
        while (true) {
            log.info("Running")
            val records: ConsumerRecords<String, String> = consumer.poll(2000)
            for (record in records) {
                log.info("Key: ${record.key()}  Value: ${record.value()}  Partition: ${record.partition()}  Offset: ${record.offset()}  ")
            }
        }
    }
    */
}