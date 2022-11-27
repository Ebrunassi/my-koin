package com.study.mykoin.core.fiis.kafka.config

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.errors.TopicExistsException
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ExecutionException

@Component
class KafkaFactory() {
    private var kafkaProducer: KafkaProducer<String, String>? = null
    private var kafkaConsumer: KafkaConsumer<String, String>? = null
    private var producerProperties: Properties? = null
    private var consumerProperties: Properties? = null

    @Value("\${kafka.host}")
     lateinit var kafkaHost: String
    @Value("\${kafka.port}")
    private lateinit var kafkaPort: String

    fun getProducerProperties(): Properties {
        if(producerProperties == null) {
            val prop = Properties()
            prop[BOOTSTRAP_SERVERS_CONFIG] = "$kafkaHost:$kafkaPort"
            prop[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.qualifiedName
            prop[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.qualifiedName

            // Set safe producer configs (it's necessary only for Kafka <= 2.8)
            prop[ENABLE_IDEMPOTENCE_CONFIG] = true
            prop[ACKS_CONFIG] = "all"
            prop[RETRIES_CONFIG] = Integer.toString(Integer.MAX_VALUE)

            // Set high throughput producer configs
            prop[LINGER_MS_CONFIG] = "20"
            prop[BATCH_SIZE_CONFIG] = Integer.toString(32 * 1024)      // 32KB
            prop[COMPRESSION_TYPE_CONFIG] = "snappy"
            producerProperties = prop
        }
        return producerProperties!!
    }

    fun getConsumerProperties(): Properties {
        if(consumerProperties == null) {
            val prop = Properties()
            prop[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$kafkaHost:$kafkaPort"
            prop[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
            prop[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
            prop[ConsumerConfig.GROUP_ID_CONFIG] = "kotlin_example_group_1"
            prop[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

            consumerProperties = prop
        }
        return consumerProperties!!
    }


    /**
     * Not used at the moment, but it was tested and it creates a topic automatically
     */
    private fun createTopic(topic: String, partitions: Int, replication: Short, cloudConfig: Properties) {
        val newTopic = NewTopic(topic, partitions, replication)
        try {
            with(AdminClient.create(cloudConfig)) {
                createTopics(listOf(newTopic)).all().get()
            }
        } catch (e: ExecutionException) {
            if (e.cause !is TopicExistsException) throw e
        }
    }
}