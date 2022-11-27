package com.study.mykoin.core

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.concurrent.Executors
import kotlin.system.exitProcess


@SpringBootApplication
class MyKoinApplication(){

    private val logger = LoggerFactory.getLogger("Application")
    private lateinit var job: Job


    suspend fun start() {
        logger.info("Starting My Koin...") // TODO - Write some fancy MyKoin logo
        val context = Executors.newFixedThreadPool(3).asCoroutineDispatcher()
        job = coroutineScope {
            launch (context) {
                startKafkaProducer(this)
            }
        }
    }

    suspend fun startKafkaProducer(coroutineScope: CoroutineScope) { }

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
