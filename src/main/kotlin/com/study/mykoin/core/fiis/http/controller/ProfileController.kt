package com.study.mykoin.core.fiis.http.controller

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.common.response.ServiceResponse
import com.study.mykoin.core.fiis.model.ProfileDTO
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.core.kafka.TopicEnum
import com.study.mykoin.core.kafka.sendMessage
import com.study.mykoin.helper.handleCall
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono

@Component
class ProfileController {
    // TODO - Continue from here. Create an POST and GET endpoint
    private val logger : Logger = LoggerFactory.getLogger(FiiController::class.java)

    @Autowired
    private lateinit var factory: KafkaFactory

    suspend fun createProfile(request: ServerRequest): ServerResponse =
        mapToProfileDTO(request)
            .map { profileDTO ->
                profileDTO.apply {
                    fiiWallet = emptyList<String>()
                    password = "******"     // TODO - hide password here
                    monthlyIncome = 0.toDouble()
                    totalInvested = 0.toDouble()
                }
            }.flatMap {
                factory.getProducer().sendMessage(
                    TopicEnum.USER_PROFILE_TOPIC.topicName,
                    it.username,
                    jsonMapper().writeValueAsString(it),
                    logger
                )
                ServiceResponse.EventSubmited("The event was sent successfully!").right()
            }
            .handleCall()

    suspend fun mapToProfileDTO(request: ServerRequest): Either<ServiceErrors, ProfileDTO> =
        request.bodyToMono(ProfileDTO::class.java).awaitSingle().right()
}