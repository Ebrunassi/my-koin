package com.study.mykoin.adapters.consumers

import com.study.mykoin.ports.outbound.FiiStorage
import com.study.mykoin.usecases.kafka.KafkaFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * TODO - rename this service to FiiConsumer, then it could call a service that will handle with the information
 */
@Service
class FiiWalletService {
    private val logger = LoggerFactory.getLogger(FiiWalletService::class.java)
    @Autowired
    private lateinit var fiiStorage: FiiStorage
    @Autowired
    lateinit var factory: KafkaFactory

    /**
     * key = fii's name
     * value = extracted information
     */
    /*
    override fun handler(key: String, record: String) =
        record.mapToFii()
            .let { fii ->
                fiiStorage.findByName(key).map {
                        try {
                            it?.apply {
                                if (fii.lastIncome.value != null) { it.lastIncome = fii.lastIncome }        // Information found by krawler
                                if (fii.nextIncome.value != null) { it.nextIncome = fii.nextIncome }        // Information found by krawler

                                if (it.actualMonth.timeWindow != actualTimeWindow()) {                      // If the month changes, we need to get everything from nextMonth and set it to actualMonth
                                    it.actualMonth = it.nextMonth.copy()
                                    it.nextMonth = MonthInformation()
                                }

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
                        fiiUpdated?.let {       // Continue analizing from here, is it working?
                            fiiStorage.upsert(it).map {
                                if (it > 0) {
                                    factory.getProducer().dispatchEvent(DomainEvent.FiiInformationUpdated(fiiUpdated, fiiUpdated.userId)) // It will dispatch only those fiis which got new information from crawler
                                }
                            }
                            logger.info("[WALLET-STORAGE] '${fiiUpdated.name}' - updated values after Krawler execution")
                        }
                    }
            }.handle()
     */
}
