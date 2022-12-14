package com.study.mykoin.core.fiis.http.controller

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.core.fiis.model.FiiEntryDTO
import kotlinx.coroutines.reactor.awaitSingle
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
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.common.response.ServiceResponse
import com.study.mykoin.core.kafka.TopicEnum
import com.study.mykoin.core.kafka.sendMessage
import com.study.mykoin.helper.handleCall
import java.util.*

@Component
class FiiController {

    private val logger : Logger = LoggerFactory.getLogger(FiiController::class.java)

    @Autowired
    private lateinit var factory: KafkaFactory

    private class Response(val description: String)

    // TODO - Find a better place to this function
    fun getActualDate(): String? {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        return dateFormat.format(Calendar.getInstance().time)
    }

    suspend fun postEntry(request: ServerRequest): ServerResponse =
        mapToEntryDTO(request)
            .map { fiiDTO ->
                fiiDTO.apply {
                    totalInvested = averagePrice * quantity
                    transactionDate = transactionDate ?: getActualDate()
                }
            }
         .flatMap {
             factory.getProducer().sendMessage(
                 TopicEnum.FIIS_HISTORY_TOPIC.topicName,
                 it.name,
                 jsonMapper().writeValueAsString(it),
                 logger
             )

             //ServiceErrors.BadRequest("Erro!!!!").left()
             ServiceResponse.EventSubmited("The event was sent successfully!").right()
        }
         .handleCall()


    suspend fun getEntry(request: ServerRequest): ServerResponse {
        return ServerResponse.status(HttpStatus.OK).bodyValueAndAwait("")
        // return ServerResponse.status(HttpStatus.OK).json().bodyAndAwait()        // Read https://www.baeldung.com/kotlin/spring-boot-kotlin-coroutines
    }

    private suspend fun mapToEntryDTO(request: ServerRequest): Either<ServiceErrors,FiiEntryDTO> =
        request.bodyToMono(FiiEntryDTO::class.java).awaitSingle().right()
}
