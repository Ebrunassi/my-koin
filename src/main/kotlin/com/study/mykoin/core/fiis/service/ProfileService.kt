package com.study.mykoin.core.fiis.service

import com.study.mykoin.core.fiis.helpers.mapToFii
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.core.fiis.storage.ProfileStorage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProfileService: ConsumerHandler {

    private val logger = LoggerFactory.getLogger(FiiWalletService::class.java)
    @Autowired
    private lateinit var profileStorage: ProfileStorage
    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage

    /**
     * key = fii's name
     * value = extracted information (Fii)
     *
     * Updates 'monthlIncome' and 'totalInvested'
     */
    override fun handler(key: String, record: String) {
        val fii = record.mapToFii()
        logger.info("Received new message in profile service: $record")
        // TODO - What information should this consumer consumes and handles?
    }
}