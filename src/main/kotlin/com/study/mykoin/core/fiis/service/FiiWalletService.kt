package com.study.mykoin.core.fiis.service

import com.study.mykoin.core.fiis.helpers.mapToFii
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FiiWalletService: ConsumerHandler {
    private val logger = LoggerFactory.getLogger(FiiWalletService::class.java)
    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage

    /**
     * key = fii's name
     * value = extracted information
     */
    override fun handler(key: String, record: String) {
        val fii = record.mapToFii()

        fiiWalletStorage.findByName(key)?.let {
            if (fii.lastIncome.value != null) {
                it.lastIncome = fii.lastIncome                                  // 'lastIncome' updated
                it.monthlyIncome =  fii.lastIncome.value!! * it.quantity        // 'monthlyIncome' updated
            }

            if(fii.nextIncome.value != null) {
                it.nextIncome = fii.nextIncome                                  // 'nextIncome' updated
            }

            fiiWalletStorage.upsert(it)                                         // Upserting
            logger.info("[WALLET-STORAGE] '${it.name}' - updated values after Krawler execution")
        }
    }


}