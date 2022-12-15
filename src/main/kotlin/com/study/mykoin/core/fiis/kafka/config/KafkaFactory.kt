package com.study.mykoin.core.fiis.kafka.config

import com.fasterxml.jackson.module.kotlin.jsonMapper
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.errors.TopicExistsException
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ExecutionException

@Component
class KafkaFactory() {
    @Deprecated("When getProducerProperties gets removed, remove this attribute too") private var producerProperties: Properties? = null
    @Deprecated("When getConsumerProperties gets removed, remove this attribute too") private var consumerProperties: Properties? = null

    @Value("\${kafka.host}")
    lateinit var KAFKA_HOST: String

    @Value("\${kafka.port}")
    private lateinit var KAFKA_PORT: String

    /**
     * Returns a consumer ready to go!
     */
    fun getConsumer(consumerGroup: String, topics: Collection<String>): KafkaConsumer<String, String>{
        val consumerProperties = Properties().apply {
            this[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$KAFKA_HOST:$KAFKA_PORT"
            this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
            this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
            this[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroup
            this[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        }
        return KafkaConsumer<String, String>(consumerProperties).apply {
            subscribe(topics)
        }
    }

    fun getProducer(): KafkaProducer<String, String> {
        val producerProperties = Properties()
        producerProperties[BOOTSTRAP_SERVERS_CONFIG] = "$KAFKA_HOST:$KAFKA_PORT"
        producerProperties[KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.qualifiedName
        producerProperties[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.qualifiedName

        // Set safe producer configs (it's necessary only for Kafka <= 2.8)
        producerProperties[ENABLE_IDEMPOTENCE_CONFIG] = true
        producerProperties[ACKS_CONFIG] = "all"
        producerProperties[RETRIES_CONFIG] = Integer.toString(Integer.MAX_VALUE)

        // Set high throughput producer configs
        producerProperties[LINGER_MS_CONFIG] = "20"
        producerProperties[BATCH_SIZE_CONFIG] = Integer.toString(32 * 1024)      // 32KB
        producerProperties[COMPRESSION_TYPE_CONFIG] = "snappy"

        return KafkaProducer<String, String>(producerProperties)
    }

    @Deprecated(message = "Use 'getProducer' instead")
    fun getProducerProperties(): Properties {
        if(producerProperties == null) {
            val prop = Properties()
            prop[BOOTSTRAP_SERVERS_CONFIG] = "$KAFKA_HOST:$KAFKA_PORT"
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

    @Deprecated(message = "Use 'getConsumer' instead")
    fun getConsumerProperties(consumerGroup: String): Properties {
        if(consumerProperties == null) {
            val prop = Properties()
            prop[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$KAFKA_HOST:$KAFKA_PORT"
            prop[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
            prop[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
            prop[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroup
            prop[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

            consumerProperties = prop
        }
        return consumerProperties!!
    }


    /**
     * Not used at the moment, but it was tested and creates a topic automatically
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

fun <K,V> KafkaProducer<K,V>.sendMessage (topicName: String, key: K, value: V){
    val producerRecord: ProducerRecord<K, V> = ProducerRecord(topicName, key, value)
    this.use { producer ->
        producer.send(producerRecord) {
                record: RecordMetadata, exception: Exception? ->
            if(exception != null && exception.stackTrace.isNotEmpty()) {
                throw Exception("Error in trying to send a message")
            }
        }
    }
}