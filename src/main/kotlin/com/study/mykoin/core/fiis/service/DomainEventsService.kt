package com.study.mykoin.core.fiis.service

import com.study.mykoin.core.fiis.domain.events.EventType
import com.study.mykoin.core.fiis.helpers.mapToFii
import com.study.mykoin.core.kafka.KafkaFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DomainEventsService : ConsumerHandler {

    private val logger = LoggerFactory.getLogger(DomainEventsService::class.java)
    @Autowired
    lateinit var factory: KafkaFactory
    @Autowired
    lateinit var profileService: ProfileService

    // Key: kind of event enum
    // Value: content
    override fun handler(key: String, record: String) {
        when (EventType.valueOf(key)) {
            EventType.FII_INFORMATION_UPDATED -> { profileService.updatedFiiInformationHandler(record.mapToFii()) }
            EventType.PROFILE_UPDATE_MONTHINCOME -> {}
            // TODO - Update with more events handler in the future
        }
    }
}
