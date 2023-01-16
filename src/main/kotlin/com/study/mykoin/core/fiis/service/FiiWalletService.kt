package com.study.mykoin.core.fiis.service

import arrow.core.*
import arrow.core.continuations.either
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.fiis.domain.events.DomainEvent
import com.study.mykoin.core.fiis.helpers.mapToFii
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.core.kafka.dispatchEvent
import com.study.mykoin.helper.handle
import kotlinx.coroutines.reactive.publish
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FiiWalletService : ConsumerHandler {
    private val logger = LoggerFactory.getLogger(FiiWalletService::class.java)
    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage
    @Autowired
    lateinit var factory: KafkaFactory

    /**
     * key = fii's name
     * value = extracted information
     */
    override fun handler(key: String, record: String)  =
        record.mapToFii()
            .let { fii ->
                fiiWalletStorage.findByName(key)
                .map {
                    it?.apply {
                        if (fii.lastIncome.value != null) {
                            it.lastIncome = fii.lastIncome // 'lastIncome' updated
                            it.monthlyIncome = fii.lastIncome.value!! * it.quantity // 'monthlyIncome' updated
                        }

                        if (fii.nextIncome.value != null) {
                            it.nextIncome = fii.nextIncome // 'nextIncome' updated
                        }
                    }
                }
                .map { it ->
                    it?.let {
                        fiiWalletStorage.upsert(it).map {
                            if (it > 0) {
                                factory.getProducer().dispatchEvent(DomainEvent.FiiInformationUpdated(fii, fii.userId))     // It will dispatch only those fiis which got new information from crawler
                            }
                        }
                        logger.info("[WALLET-STORAGE] '${it.name}' - updated values after Krawler execution")
                    }
                }
        }.handle()
}
