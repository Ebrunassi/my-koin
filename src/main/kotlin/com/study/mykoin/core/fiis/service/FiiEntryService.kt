package com.study.mykoin.core.fiis.service

import arrow.core.*
import arrow.core.continuations.either
import com.study.mykoin.core.common.response.ServiceResponse
import com.study.mykoin.core.crawler.model.LastIncome
import com.study.mykoin.core.crawler.model.NextIncome
import com.study.mykoin.core.fiis.helpers.mapToFii
import com.study.mykoin.core.fiis.helpers.mapToFiiEntry
import com.study.mykoin.core.fiis.storage.FiiHistoryStorage
import com.study.mykoin.core.fiis.storage.FiiWalletStorage
import com.study.mykoin.core.fiis.storage.ProfileStorage
import com.study.mykoin.domain.fiis.MonthInformation
import com.study.mykoin.domain.fiis.createFii
import com.study.mykoin.domain.fiis.updateFii
import com.study.mykoin.helper.handle
import com.study.mykoin.helper.otherwise
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FiiEntryService : ConsumerHandler {
    private val logger = LoggerFactory.getLogger(FiiEntryService::class.java)

    @Autowired
    private lateinit var fiiHistoryStorage: FiiHistoryStorage
    @Autowired
    private lateinit var fiiWalletStorage: FiiWalletStorage
    @Autowired
    private lateinit var profileStorage: ProfileStorage

    @Deprecated("This method will be deprecated soon. It does not make sense to handle entry information async.")
    override fun handler(key: String, record: String) {
        // try {
        record.mapToFiiEntry()
            .flatMap { fiiEntry ->
                fiiHistoryStorage.save(fiiEntry).also { logger.info("New entry received '${fiiEntry.name}'") }
            }.flatMap { fiiEntry ->
                fiiWalletStorage.findByName(fiiEntry.name).flatMap {
                    it?.let {
                        it.updateFii(fiiEntry)
                        fiiWalletStorage.upsert(it).also { modified ->
                            logger.info("[WALLET-STORAGE] '${fiiEntry.name}' got updated! ($modified documents got modified)")
                        }
                    }.otherwise {
                        fiiWalletStorage.save(record.mapToFii())
                            //.flatMap { profileStorage.upsertFiiWallet(fiiEntry.userId, it.id!!) }
                            .also {
                                logger.info("[WALLET-STORAGE] Inserted '${fiiEntry.name}' in the wallet")
                                logger.info("[PROFILE-STORAGE] Updated profile with the new fii '${fiiEntry.id}'")
                            }
                    }
                }
            }.handle()
    }

    fun syncHandler(record: String) = either.eager {
            record.mapToFiiEntry()
                .map { fiiEntry -> profileStorage.findById(fiiEntry.userId).bind()      // Short circuit here whether it doesn't find any profile
                    fiiEntry
                }.flatMap { fiiEntry ->
                    fiiWalletStorage.findByName(fiiEntry.name).flatMap {
                        it?.let {
                            it.updateFii(fiiEntry)
                            fiiWalletStorage.upsert(it).also { modified -> logger.info("[WALLET-STORAGE] '${fiiEntry.name}' got updated! ($modified documents got modified)")
                            }
                        }.otherwise {
                            record.mapToFii()
                                .apply {
                                    this.nextMonth = MonthInformation()
                                    this.actualMonth = MonthInformation()
                                    this.lastIncome = LastIncome()
                                    this.nextIncome = NextIncome()
                                }
                                .also {
                                    it.createFii(record.mapToFiiEntry().getOrHandle { throw Exception("") })
                                }
                                .let {
                                    fiiWalletStorage.save(it)
                                        //.flatMap { profileStorage.upsertFiiWallet(fiiEntry.userId, it.id!!) }     There is no need to insert FII id in Profile collection
                                            .also { logger.info("[WALLET-STORAGE] Inserted '${fiiEntry.name}' in the wallet")
                                        //logger.info("[PROFILE-STORAGE] Updated profile with the new fii '${fiiEntry.id}'")
                                    }
                                }
                        }.flatMap {
                            fiiHistoryStorage.save(fiiEntry)
                                .also { logger.info("New entry received '${fiiEntry.name}'") }
                        }.map {
                            ServiceResponse.EntryCreated("The entry ${fiiEntry.name} has been created successfully")
                        }
                    }
                }.bind()
    }
}
