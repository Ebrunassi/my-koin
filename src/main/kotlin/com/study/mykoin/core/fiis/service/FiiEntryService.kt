package com.study.mykoin.core.fiis.service

import com.study.mykoin.core.fiis.storage.FiiHistoryStorage
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.domain.fiis.updateFii
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.fiis.helpers.mapToFii
import com.study.mykoin.core.fiis.helpers.mapToFiiEntry
import com.study.mykoin.core.fiis.storage.ProfileStorage
import com.study.mykoin.helper.otherwise

import org.springframework.stereotype.Service

@Service
class FiiEntryService: ConsumerHandler {
    private val logger = LoggerFactory.getLogger(FiiEntryService::class.java)

    @Autowired
    private lateinit var fiiHistoryStorage: FiiHistoryStorage
    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage
    @Autowired
    private lateinit var profileStorage: ProfileStorage


    override fun handler(key: String, record: String){
        try {
            val fiiEntry = record.mapToFiiEntry()
            fiiHistoryStorage.save(fiiEntry).also { logger.info("New entry received '${fiiEntry.name}'") }       // Save FII in log table

            fiiWalletStorage.findByName(fiiEntry.name)                         // Checks if it already exists in wallet collection
                ?.let {
                    it.updateFii(fiiEntry)
                    fiiWalletStorage.upsert(it)                                 // If it exists, update the wallet
                        .also { modified ->
                            logger.info("[WALLET-STORAGE] '${fiiEntry.name}' got updated! ($modified documents got modified)")
                        }
                }.otherwise {
                    fiiWalletStorage.save(record.mapToFii()).let {              // Saves a new record if didn't find anything and also update the information in profile's collection
                        profileStorage.upsert(fiiEntry.userId, it.id!!)
                    //profileStorage.upsert(fiiEntity.name, it.id!!)
                    }.also {
                        logger.info("[WALLET-STORAGE] Inserted '${fiiEntry.name}' in the wallet")
                        logger.info("[PROFILE-STORAGE] Updated profile with the new fii '${fiiEntry.id}'")
                    }
                }

        } catch(e: Exception) {
            logger.error(e.message)
            e.printStackTrace()
            ServiceErrors.BadRequest("Erro!!!!")
        }
    }
}
