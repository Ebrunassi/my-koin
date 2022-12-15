package com.study.mykoin.core.fiis.service

import com.google.gson.Gson
import com.study.mykoin.core.fiis.storage.FiiHistoryStorage
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.domain.fiis.Fii
import com.study.mykoin.domain.fiis.FiiEntry
import com.study.mykoin.domain.fiis.updateFii
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.study.mykoin.core.common.errors.ServiceErrors

import org.springframework.stereotype.Service

@Service
class EntryService {
    private val logger = LoggerFactory.getLogger("EntryService")

    @Autowired
    private lateinit var fiiHistoryStorage: FiiHistoryStorage
    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage


    fun handler(record: String){
        logger.info("Received new message: $record")
        try {
            val fiiEntity = record.mapToFiiEntity()
            fiiHistoryStorage.save(fiiEntity)                                   // Save FII in log table

            val fii = fiiWalletStorage.findByName(fiiEntity.name)        // Checks if it already exists on wallet collection
                ?.let {
                    it.updateFii(fiiEntity)
                    fiiWalletStorage.upsert(it)
                        .also { modified ->
                            logger.info("${fiiEntity.name} got updated! ($modified documents got modified)")
                        }
                }
            fii ?: fiiWalletStorage.save(record.mapToFii())                     // Saves a new record if didn't find anything

        } catch(e: Exception) {
            logger.error(e.message)
            e.printStackTrace()
            ServiceErrors.BadRequest("Erro!!!!")
        }
    }
}

fun String.mapToFiiEntity(): FiiEntry {
    return Gson().fromJson(this, FiiEntry::class.java)
}

fun String.mapToFii(): Fii {
    return Gson().fromJson(this, Fii::class.java)
}