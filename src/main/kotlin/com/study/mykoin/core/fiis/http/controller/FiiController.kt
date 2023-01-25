package com.study.mykoin.core.fiis.http.controller

import arrow.core.*
import arrow.core.continuations.either
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.fiis.model.FiiEntryDTO
import com.study.mykoin.core.fiis.service.FiiEntryService
import com.study.mykoin.core.fiis.storage.ProfileStorage
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.helper.handleCall
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
import java.util.*

@Component
class FiiController {

    private val logger: Logger = LoggerFactory.getLogger(FiiController::class.java)

    @Autowired
    private lateinit var factory: KafkaFactory
    @Autowired
    private lateinit var profileStorage: ProfileStorage
    @Autowired
    private lateinit var fiiEntryService: FiiEntryService

    private class Response(val description: String)
    fun getActualDate(): String? {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        return dateFormat.format(Calendar.getInstance().time)
    }

    suspend fun postEntry(request: ServerRequest): ServerResponse =
        mapToEntryDTO(request)
            .map {
                profileStorage.findById(it.userId)
                it
            }
            .map { fiiDTO ->
                fiiDTO.apply {
                    totalInvested = averagePrice * quantity
                    transactionDate = transactionDate ?: getActualDate()
                }
            }
            .flatMap {
                fiiEntryService.syncHandler(jsonMapper().writeValueAsString(it))
            }
            .handleCall()

    suspend fun getEntry(request: ServerRequest): ServerResponse {
        return ServerResponse.status(HttpStatus.OK).bodyValueAndAwait("")
        // return ServerResponse.status(HttpStatus.OK).json().bodyAndAwait()        // Read https://www.baeldung.com/kotlin/spring-boot-kotlin-coroutines
    }

    private suspend fun mapToEntryDTO(request: ServerRequest): Either<ServiceErrors, FiiEntryDTO> =
        either.runCatching {
            request.bodyToMono(FiiEntryDTO::class.java).awaitSingle().right()
        }.getOrElse { e ->
            ServiceErrors.BadRequest(e.message ?: "An error happened when mapping the request body").left()
        }
}
