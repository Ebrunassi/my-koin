package com.study.mykoin.core

import com.study.mykoin.core.crawler.Krawler
import com.study.mykoin.core.fiis.consumer.DomainEventsConsumer
import com.study.mykoin.core.fiis.consumer.FiiEntryConsumer
import com.study.mykoin.core.fiis.consumer.FiiWalletConsumer
import com.study.mykoin.core.fiis.consumer.ProfileConsumer
import io.thelandscape.krawler.crawler.KrawlConfig
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.concurrent.Executors
import kotlin.system.exitProcess

@SpringBootApplication
class MyKoinApplication() {

    private val logger = LoggerFactory.getLogger("Application")
    private lateinit var job: Job

    suspend fun start() {
        logger.info("Starting My Koin...") // TODO - Write some fancy MyKoin logo
        val context = Executors.newFixedThreadPool(3).asCoroutineDispatcher()
        job = coroutineScope {
            launch(context) {
                startKafkaProducer(this)
                startKafkaConsumer(this)
                startKrawler(this)
            }
        }
    }

    // Maybe it can be used later
    @Bean
    fun initKrawlerConfig(): KrawlConfig {
        return KrawlConfig(totalPages = 6, maxDepth = 1)
    }
    suspend fun startKafkaProducer(coroutineScope: CoroutineScope) { }
    suspend fun startKafkaConsumer(coroutineScope: CoroutineScope) {
        logger.info("Starting consumers...")
        FiiEntryConsumer().init().also { logger.info("FiiEntryConsumer has been started successfully") }
        FiiWalletConsumer().init().also { logger.info("FiiWalletConsumer has been started successfully") }
        //ProfileConsumer().init().also { logger.info("ProfileConsumer has been started successfully") }
        DomainEventsConsumer().init().also { logger.info("ProfileConsumer has been started successfully") }
        logger.info("All consumers has been started!")
    }
    suspend fun startKrawler(coroutineScope: CoroutineScope) {
        try {
            Krawler().init()
            logger.info("The Krawler routine has been set to start automatically")
        } catch (e: Exception) {
            logger.error("Error while instantiating krawler: ${e.message}")
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
