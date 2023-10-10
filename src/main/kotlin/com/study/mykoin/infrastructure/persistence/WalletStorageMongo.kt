package com.study.mykoin.infrastructure.persistence

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import com.study.mykoin.domain.fiis.fii.FiiAggregate
import com.study.mykoin.domain.fiis.fii.FiiId
import com.study.mykoin.domain.fiis.profile.Profile
import com.study.mykoin.domain.fiis.profile.ProfileId
import com.study.mykoin.domain.fiis.wallet.Wallet
import com.study.mykoin.domain.fiis.wallet.WalletId
import com.study.mykoin.ports.outbound.WalletStorage
import com.study.mykoin.usecases.ServiceErrors
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class WalletStorageMongo: WalletStorage {
    private val logger = LoggerFactory.getLogger(WalletStorageMongo::class.java)
    @Autowired
    private lateinit var mongo: MongoOperations
    override fun save(wallet: Wallet): Either<ServiceErrors, Wallet> {
        return mongo.save(wallet).right()
    }

    override fun findByFiiId(id: WalletId): Either<ServiceErrors, Wallet> {
        val wallet = mongo.findById(id.value.toString(), Wallet::class.java)
        return wallet?.right() ?: ServiceErrors.EntityNotFound("Wallet '${id.value} has not been found").left()
    }

    override fun findAggregateIdByFiiId(walletId: WalletId, id: String): FiiAggregate? {
        val query = Query(Criteria.where("fiiId").`is`(id)
            .and("walletId").`is`(walletId.value.toString())
        )
        return mongo.findOne(query, FiiAggregate::class.java)

    }

    /**
     * As Wallet is the Root Aggregate, every query to FiiAggregate should
     * be made through Wallet class
     */
    override fun updateFiiAggregate(fiiAggregate: FiiAggregate, fiiId: String): Either<ServiceErrors, Long> =
        either.runCatching {
            val query = Query(Criteria.where("walletId").`is`(fiiAggregate.walletId).and("fiiId").`is`(fiiId))
            val update = Update()
                .set("id", fiiAggregate.id)
                .set("fiiId", fiiAggregate.fiiId)
                .set("quantity", fiiAggregate.quantity)
                .set("totalInvested", fiiAggregate.totalInvested)
                .set("averagePrice", fiiAggregate.averagePrice)
                .set("porcent", fiiAggregate.porcent)

            // Is this running?
            val queryWallet = Query(Criteria.where("id").`is`(fiiAggregate.walletId))
            val updateWallet = Update()
                .addToSet("owningFiis", fiiAggregate.id)

            mongo.upsert(query, update, FiiAggregate::class.java).modifiedCount.right()
            mongo.updateFirst(queryWallet, updateWallet, Wallet::class.java).modifiedCount.right()

        }.getOrElse {
            ServiceErrors.InternalError(
                it.message ?: "Error in upserting data in database: ${it.localizedMessage}"
            ).left()
        }
}