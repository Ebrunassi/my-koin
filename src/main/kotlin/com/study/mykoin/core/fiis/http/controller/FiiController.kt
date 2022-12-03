package com.study.mykoin.core.fiis.http.controller

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.study.mykoin.core.fiis.kafka.config.KafkaFactory
import com.study.mykoin.core.fiis.model.FiiDTO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class FiiController {

    private val log : Logger = LoggerFactory.getLogger("FiiController")

    @Autowired
    private lateinit var factory: KafkaFactory

    companion object {
        const val FIIS_TOPIC = "fiis_topic"
    }

    private class Response(val description: String)

    // TODO - Find a better place to this function
    fun getActualDate(): String? {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        return dateFormat.format(Calendar.getInstance().time)
    }

    suspend fun postEntry(request: ServerRequest): ServerResponse {
        mapToEntryDTO(request).apply {
            totalInvested = averagePrice * quantity
            transactionDate = transactionDate ?: getActualDate()
        }.let {

            val producerRecord: ProducerRecord<String, String> = ProducerRecord(
                FIIS_TOPIC,
                it.name,                                // Key
                jsonMapper().writeValueAsString(it)     // Value
            )

            KafkaProducer<String, String>(factory.getProducerProperties()).use { producer ->        // A new Kafka producer is created and closed after each iteration
                producer.send(producerRecord) {
                        record: RecordMetadata, exception: Exception? ->
                    if(exception == null || exception.stackTrace.isEmpty()){
                        log.info("Creating a new message. Topic '${record.topic()}, partition: '${record.partition()}' offset '${record.offset()}' ")
                    }else
                        log.error("no message received: '${exception}'")
                }
            }
        }
        return ServerResponse.status(HttpStatus.CREATED).bodyValueAndAwait(Response("The event was sent successfully!"))
    }

    suspend fun getEntry(request: ServerRequest): ServerResponse {
        return ServerResponse.status(HttpStatus.OK).bodyValueAndAwait("")
        // return ServerResponse.status(HttpStatus.OK).json().bodyAndAwait()        // Read https://www.baeldung.com/kotlin/spring-boot-kotlin-coroutines
    }

    private suspend fun mapToEntryDTO(request: ServerRequest): FiiDTO =
        request.bodyToMono(FiiDTO::class.java).awaitSingle()
}
