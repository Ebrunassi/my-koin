package com.study.mykoin.core.fiis.service

import arrow.core.continuations.nullable
import com.google.gson.Gson
import com.study.mykoin.core.fiis.storage.FiiHistoryStorage
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.domain.fiis.Fii
import com.study.mykoin.domain.fiis.FiiEntry
import com.study.mykoin.domain.fiis.updateFii
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.fiis.helpers.mapToFii
import com.study.mykoin.core.fiis.helpers.mapToFiiEntity
import com.study.mykoin.core.fiis.storage.ProfileStorage
import com.study.mykoin.helper.otherwise

import org.springframework.stereotype.Service

@Service
class EntryService: ConsumerHandler {
    private val logger = LoggerFactory.getLogger(EntryService::class.java)

    @Autowired
    private lateinit var fiiHistoryStorage: FiiHistoryStorage
    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage
    @Autowired
    private lateinit var profileStorage: ProfileStorage


    override fun handler(key: String, record: String){
        try {
            val fiiEntity = record.mapToFiiEntity()
            fiiHistoryStorage.save(fiiEntity)                                   // Save FII in log table

            fiiWalletStorage.findByName(fiiEntity.name)                         // Checks if it already exists in wallet collection
                ?.let {
                    it.updateFii(fiiEntity)
                    fiiWalletStorage.upsert(it)                                 // If it exists, update the wallet
                        .also { modified ->
                            logger.info("${fiiEntity.name} got updated! ($modified documents got modified)")
                        }
                }.otherwise {
                    fiiWalletStorage.save(record.mapToFii()).let {              // Saves a new record if didn't find anything and also update the information in profile's collection
                        profileStorage.upsert(fiiEntity.name, it.id!!)
                    }
                }

        } catch(e: Exception) {
            logger.error(e.message)
            e.printStackTrace()
            ServiceErrors.BadRequest("Erro!!!!")
        }
    }
}
