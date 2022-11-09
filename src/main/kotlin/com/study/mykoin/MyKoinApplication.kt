package com.study.mykoin

import com.study.mykoin.api.http.controller.FiiController
import com.study.mykoin.kafka.config.KafkaFactory
import com.study.mykoin.kafka.consumer.EntryConsumer
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.Scheduled
import reactor.core.publisher.Signal.subscribe
import java.time.Duration.ofMillis
import java.util.*
import java.util.concurrent.Executors
import kotlin.system.exitProcess

@SpringBootApplication
class MyKoinApplication {

    private val kafkaFactory by lazy { KafkaFactory() }
    //@Autowired
    //private lateinit var entryConsumer: EntryConsumer
    private val logger = LoggerFactory.getLogger("Application")
    private lateinit var job: Job

    private var kafkaHost: String? = "127.0.0.1"                  // TODO - hardcoded variable
    private val kafkaPort: String = "29092"
    suspend fun start() {
        logger.info("Starting My Koin...") // TODO - Write some fancy MyKoin logo
        val context = Executors.newFixedThreadPool(3).asCoroutineDispatcher()
        job = coroutineScope {
            launch (context) {
                startKafkaProducer(this)
                startKafkaConsumer(this)            // TODO - this function is blocking the whole thread flow
            }
        }
    }

    suspend fun startKafkaProducer(coroutineScope: CoroutineScope) { }

    suspend fun startKafkaConsumer(coroutineScope: CoroutineScope) {

        logger.info("Kafka host -> $kafkaHost")
        try {
            val kafkaFactory = KafkaFactory().apply { init(kafkaHost!!, kafkaPort) }
            kafkaFactory.consumer()
            /*
            val entry = EntryConsumer()
            entry.init(kafkaFactory.apply { init(kafkaHost!!, kafkaPort) })
            entry.startKafkaConsumer()
            */
        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun shutdown() {
        logger.info("My Koin is shuting down...")
        runBlocking { job.cancelAndJoin() }
        logger.info("Application shutdown complete")
    }
}

suspend fun main(args: Array<String>) {

    runApplication<MyKoinApplication>(*args)
    try {
        with(MyKoinApplication()) {
            java.lang.Runtime.getRuntime().addShutdownHook(Thread(this::shutdown))
            start()
        }
    } catch (e: Exception) {
        exitProcess(1)
    }

}
