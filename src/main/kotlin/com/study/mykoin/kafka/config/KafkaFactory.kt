package com.study.mykoin.kafka.config

import com.study.mykoin.api.http.controller.FiiController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.errors.TopicExistsException
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import java.util.concurrent.ExecutionException

@Component
class KafkaFactory() {
    private var kafkaProducer: KafkaProducer<String, String>? = null
    private var kafkaConsumer: KafkaConsumer<String, String>? = null
    private var properties: Properties? = null

    @Value("\${kafka.host}")
    private lateinit var kafkaHost: String
    @Value("\${kafka.port}")
    private lateinit var kafkaPort: String

    fun init(kafkaHost: String, kafkaPort: String) {
        this.kafkaHost = kafkaHost
        this.kafkaPort = kafkaPort
    }

    fun getProperties(): Properties {
        if(properties == null) {
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
            properties = prop
        }
        return properties!!
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

    fun consumer() {
        val topic = FiiController.FIIS_TOPIC

        // Load properties from disk.
        val props = Properties()
        // Add additional properties.
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$kafkaHost:$kafkaPort"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.GROUP_ID_CONFIG] = "kotlin_example_group_1"
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        val consumer = KafkaConsumer<String, String>(props).apply {
            subscribe(listOf(topic))
        }

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
                                newCount
                            }
                    }
                }
            }
        }
    }
}