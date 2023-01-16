package com.study.mykoin.core.fiis.service

import arrow.core.left
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.fiis.helpers.mapToProfileEntity
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.core.fiis.storage.ProfileStorage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProfileService : ConsumerHandler {

    private val logger = LoggerFactory.getLogger(FiiWalletService::class.java)
    @Autowired
    private lateinit var profileStorage: ProfileStorage
    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage

    /**
     * key = userId
     * value = extracted information (ProfileDTO)
     * Updates 'monthlIncome' and 'totalInvested'
     */
    override fun handler(key: String, record: String) {

        try {
            logger.info("Received new message in profile service: $record")
            val profileEntity = record.mapToProfileEntity()
            profileStorage.save(profileEntity)
                .map { logger.info("[PROFILE-STORAGE] Updated database with '${it.username}' informations") } // The usage of Either here is only to learn and practice, it is not necessary
            // .fold(
            //    ifLeft = { logger.error("Some problem happened when saving in database: $it") },
            //    ifRight = { logger.info("[PROFILE-STORAGE] Updated database") }
            // )
        } catch (e: Exception) {
            ServiceErrors.HandlerError("${e.message} - ${e.localizedMessage}").left()
        }
    }

}
