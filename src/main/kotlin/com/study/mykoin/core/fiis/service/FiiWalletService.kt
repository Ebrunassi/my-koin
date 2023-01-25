package com.study.mykoin.core.fiis.service

import com.study.mykoin.core.fiis.domain.events.DomainEvent
import com.study.mykoin.core.fiis.helpers.mapToFii
import com.study.mykoin.core.fiis.model.enums.WhenPaymentEnum
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.core.kafka.KafkaFactory
import com.study.mykoin.core.kafka.dispatchEvent
import com.study.mykoin.domain.fiis.nextMonthIncome
import com.study.mykoin.domain.fiis.thisMonthIncome
import com.study.mykoin.domain.fiis.whenPayment
import com.study.mykoin.helper.handle
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
    override fun handler(key: String, record: String) =
        record.mapToFii()
            .let { fii ->
                fiiWalletStorage.findByName(key)
                    .map {
                        try {
                            it?.apply {
                                if (fii.lastIncome.value != null) { it.lastIncome = fii.lastIncome }

                                if (fii.nextIncome.value != null) { it.nextIncome = fii.nextIncome }

                                with (it.thisMonthIncome()) {
                                    it.actualMonth.monthlyIncome = (this ?: 0.0) * it.actualMonth.quantity
                                    it.actualMonth.timeWindow = actualTimeWindow()
                                }

                                with (it.nextMonthIncome()) {
                                    it.nextMonth.monthlyIncome = (this ?: 0.0) * it.nextMonth.quantity
                                    it.nextMonth.timeWindow = nextTimeWindow()
                                }
                            }
                        } catch (e: Exception) { e.printStackTrace() }
                        it
                    }
                    .map { fiiUpdated ->
                        fiiUpdated?.let {
                            fiiWalletStorage.upsert(it).map {
                                if (it > 0) {
                                    factory.getProducer().dispatchEvent(DomainEvent.FiiInformationUpdated(fiiUpdated, fiiUpdated.userId)) // It will dispatch only those fiis which got new information from crawler
                                }
                            }
                            logger.info("[WALLET-STORAGE] '${fiiUpdated.name}' - updated values after Krawler execution")
                        }
                    }
            }.handle()
}
