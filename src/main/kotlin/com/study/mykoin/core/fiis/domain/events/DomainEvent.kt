package com.study.mykoin.core.fiis.domain.events

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.study.mykoin.core.kafka.TopicEnum
import com.study.mykoin.domain.fiis.Fii
import com.study.mykoin.domain.fiis.MonthIncome
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// DDD
interface DomainEvent {
    val key: String
    val value: String
    val topicName: String
    val logger: Logger

    data class FiiInformationUpdated(val fii: Fii, val userId: Long) : DomainEvent {
        override val key: String = EventType.FII_INFORMATION_UPDATED.eventType
        override val value: String = jsonMapper().writeValueAsString(fii)
        override val topicName: String = TopicEnum.DOMAIN_EVENTS_TOPIC.topicName
        override val logger: Logger = LoggerFactory.getLogger(FiiInformationUpdated::class.java)
    }

    data class ProfileUpdateMonthIncome(val monthIncome: MonthIncome, val userId: Long) : DomainEvent {

        override val key: String = EventType.PROFILE_UPDATE_MONTHINCOME.eventType
        override val value: String = jsonMapper().writeValueAsString(monthIncome)
        override val topicName: String = TopicEnum.DOMAIN_EVENTS_TOPIC.topicName
        override val logger: Logger = LoggerFactory.getLogger(FiiInformationUpdated::class.java)
    }
}
