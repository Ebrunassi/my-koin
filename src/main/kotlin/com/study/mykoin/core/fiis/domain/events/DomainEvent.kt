package com.study.mykoin.core.fiis.domain.events

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.study.mykoin.core.fiis.service.FiiWalletService
import com.study.mykoin.core.kafka.TopicEnum
import com.study.mykoin.domain.fiis.Fii
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// DDD
interface DomainEvent {
    val key: String
    val value: String
    val topicName: String
    val logger: Logger

    data class FiiInformationUpdated (val fii: Fii, val userId: Long): DomainEvent {
        override val key: String = userId.toString()
        override val value: String = jsonMapper().writeValueAsString(fii)
        override val topicName: String = EventType.FII_INFORMATION_UPDATED.eventType
        override val logger: Logger =  LoggerFactory.getLogger(FiiInformationUpdated::class.java)
    }
}