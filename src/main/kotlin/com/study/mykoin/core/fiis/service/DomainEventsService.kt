package com.study.mykoin.core.fiis.service

import com.study.mykoin.core.fiis.domain.events.EventType
import com.study.mykoin.core.fiis.helpers.mapToFii
import com.study.mykoin.core.fiis.storage.ProfileStorage
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.domain.fiis.Fii
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DomainEventsService: ConsumerHandler {

    private val logger = LoggerFactory.getLogger(DomainEventsService::class.java)
    @Autowired
    lateinit var factory: KafkaFactory
    @Autowired
    lateinit var profileStorage: ProfileStorage

    // Key: kind of event enum
    // Value: content
    override fun handler(key: String, record: String) {
        when(EventType.valueOf(key)) {
            EventType.FII_INFORMATION_UPDATED -> { updatedFiiInformationHandler(record.mapToFii()) }
        }
    }

    private fun updatedFiiInformationHandler(fii: Fii) {

        // Once I got here, it's implicit that {fii} got modified with new values, so we need to update monthlyIncome from profile
    }
}