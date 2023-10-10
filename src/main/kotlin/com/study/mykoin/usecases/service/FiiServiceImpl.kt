package com.study.mykoin.usecases.service

import arrow.core.*
import arrow.core.continuations.either
import com.study.mykoin.ports.outbound.FiiHistoryStorage
import com.study.mykoin.ports.outbound.FiiStorage
import com.study.mykoin.ports.outbound.ProfileStorage
import com.study.mykoin.domain.fiis.fii.*
import com.study.mykoin.ports.inbound.FiiService
import com.study.mykoin.ports.outbound.WalletStorage
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.usecases.service.commands.FiiCommand
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FiiServiceImpl(
    val profileStorage: ProfileStorage,
    val fiiStorage: FiiStorage,
    val walletStorage: WalletStorage
): FiiService {
    private val logger = LoggerFactory.getLogger(FiiServiceImpl::class.java)

    @Autowired
    private lateinit var fiiHistoryStorage: FiiHistoryStorage
    //@Autowired
    //private lateinit var fiiStorage: FiiStorage
    //@Autowired
    //private lateinit var profileStorage: ProfileStorage

    /*
    @Deprecated("This method will be deprecated soon. It does not make sense to handle entry information async.")
    override fun handler(key: String, record: String) {
        // try {
        record.mapToFiiEntry()
            .flatMap { fiiEntry ->
                fiiHistoryStorage.save(fiiEntry).also { logger.info("New entry received '${fiiEntry.name}'") }
            }.flatMap { fiiEntry ->
                fiiStorage.findByName(fiiEntry.name).flatMap {
                    it?.let {
                        it.updateFii(fiiEntry)
                        fiiStorage.upsert(it).also { modified ->
                            logger.info("[WALLET-STORAGE] '${fiiEntry.name}' got updated! ($modified documents got modified)")
                        }
                    }.otherwise {
                        fiiStorage.save(record.mapToFii())
                            //.flatMap { profileStorage.upsertFiiWallet(fiiEntry.userId, it.id!!) }
                            .also {
                                logger.info("[WALLET-STORAGE] Inserted '${fiiEntry.name}' in the wallet")
                                logger.info("[PROFILE-STORAGE] Updated profile with the new fii '${fiiEntry.id}'")
                            }
                    }
                }
            }.handle()
    }

    */

    @Transactional  // FII and FiiAggregate should be in the same transaction.
    override suspend fun create(fiiCommand: FiiCommand): Either<ServiceErrors, Long> = either {
        /**
         * it should:
         * - Check if FII exists in database
         * - Create FII (if it doesn't exist)
         * - create domain event to update profile with that information
         */

        // Fii can be checked straight from database because it's part of different Entity Aggregate
        // (they are independant of Wallet entity aggregate)
        val fiiId = fiiStorage.findByName(fiiCommand.name.stringValue()).bind()?.id
            ?: Fii.create(fiiCommand)
                .let { fiiStorage.save(it).bind().id } // Create FII if it doesn't exist

        // Check FiiAggregate through its Entity Aggregate (Wallet)
        val fiiAggregate =
            walletStorage
                .findAggregateIdByFiiId(fiiCommand.walletId, fiiId)
                ?.update(fiiCommand.quantity, fiiCommand.price)
                ?: FiiAggregate.new(
                    fiiId,
                    fiiCommand.walletId.stringValue(),
                    fiiCommand.quantity,
                    fiiCommand.price
                )

        walletStorage.updateFiiAggregate(fiiAggregate, fiiId).bind()
        // TODO - Create DomainEvent to update profile
    }
}
