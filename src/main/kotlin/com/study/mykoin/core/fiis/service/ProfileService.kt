package com.study.mykoin.core.fiis.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.fiis.helpers.mapToFii
import com.study.mykoin.core.fiis.helpers.mapToProfileEntity
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.core.fiis.storage.ProfileStorage
import com.study.mykoin.domain.fiis.Profile
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
     * key = username
     * value = extracted information (ProfileDTO)
     * Updates 'monthlIncome' and 'totalInvested'
     */
    override fun handler(key: String, record: String){

        try {
            logger.info("Received new message in profile service: $record")
            val profileEntity = record.mapToProfileEntity()
            profileStorage.save(profileEntity).also { logger.info("Updated database") }.right()
        } catch (e: Exception) {
            ServiceErrors.HandlerError("${e.message} - ${e.localizedMessage}").left()
        }
    }
}