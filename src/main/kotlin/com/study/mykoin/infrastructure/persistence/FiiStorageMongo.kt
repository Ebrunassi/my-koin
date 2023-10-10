package com.study.mykoin.infrastructure.persistence

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.domain.fiis.fii.Fii
import com.study.mykoin.ports.outbound.FiiStorage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class FiiStorageMongo : FiiStorage {
    private val logger = LoggerFactory.getLogger(FiiHistoryStorageMongo::class.java)
    @Autowired
    private lateinit var mongo: MongoOperations
    @Autowired
    private lateinit var sequenceGenerator: SequenceGenerator
    override fun save(fii: Fii): Either<ServiceErrors, Fii> {
        return try {
            logger.info("Saving $fii in database")
            mongo.save(fii).also { logger.info("Saved successfully") }.right()
        } catch (e: Exception) {
            ServiceErrors.InternalError(e.message ?: "Error in searching data by name in database: ${e.localizedMessage}").left()
        }
    }

    override fun findById(id: Long): Fii? {
        return mongo.findById(id, Fii::class.java)
    }

    override fun findByUserId(userId: Long): List<Fii>? {
        val query = Query(Criteria.where("userId").`is`(userId))
        return mongo.find(query, Fii::class.java)
    }

    override fun findAll(): List<Fii>? =
        mongo.findAll(Fii::class.java)

    override fun findByName(name: String): Either<ServiceErrors, Fii?> =
        try {
            val query = Query(Criteria.where("name").`is`(name))
            mongo.find(query, Fii::class.java).first().right()
        } catch (e: Exception) {
            null.right().also { logger.info("[WALLET-STORAGE] There is no FII named $name") }
            //ServiceErrors.FiiWalletNotFound(e.message ?: "There was not found any FII with name $name").left()
        }

    override fun upsert(fii: Fii): Either<ServiceErrors, Long> {
        TODO("Not yet implemented")
    }

    /*
        override fun upsert(fii: Fii): Either<ServiceErrors, Long> =
            either.runCatching {
                val query = Query(Criteria.where("name").`is`(fii.name))
                val update = Update()
                    .set("quantity", fii.quantity)
                    .set("totalInvested", fii.totalInvested)
                    .set("averagePrice", fii.averagePrice)
                    .set("actualMonth", fii.actualMonth)
                    .set("nextMonth", fii.nextMonth)
                    .set("lastIncome", fii.lastIncome)
                    .set("nextIncome", fii.nextIncome)

                mongo.updateFirst(query, update, Fii::class.java).modifiedCount.right() // It will return > 0 if something was really changed from the previous data
            }.getOrElse {
                ServiceErrors.InternalError(it.message ?: "Error in upserting data in database: ${it.localizedMessage}").left()
            }

     */
}
