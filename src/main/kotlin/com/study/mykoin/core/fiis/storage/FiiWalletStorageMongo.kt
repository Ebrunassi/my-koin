package com.study.mykoin.core.fiis.storage

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.common.storage.SequenceGenerator
import com.study.mykoin.domain.fiis.Fii
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class FiiWalletStorageMongo : FiiWalletStorage {
    private val logger = LoggerFactory.getLogger(FiiHistoryStorageMongo::class.java)
    @Autowired
    private lateinit var mongo: MongoOperations
    @Autowired
    private lateinit var sequenceGenerator: SequenceGenerator
    override fun save(fii: Fii): Either<ServiceErrors, Fii> {
        return try {
            fii.id = sequenceGenerator.genereteSequence(Fii.SEQUENCE_NAME)
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

    override fun findByName(name: String): Either<ServiceErrors, Fii?> {
        return try {
            val query = Query(Criteria.where("name").`is`(name))
            return mongo.find(query, Fii::class.java).firstOrNull().right()
        } catch (e: Exception) {
            ServiceErrors.InternalError(e.message ?: "Error in searching data by name in database: ${e.localizedMessage}").left()
        }
    }

    override fun upsert(fii: Fii): Either<ServiceErrors, Long> =
        either.runCatching {
            val query = Query(Criteria.where("name").`is`(fii.name))
            val update = Update()
                .set("quantity", fii.quantity)
                .set("totalInvested", fii.totalInvested)
                .set("averagePrice", fii.averagePrice)
                .set(
                    "monthlyIncome",
                    fii.monthlyIncome
                ) // TODO - Probably we will need to set new values for other attributes too
                .set("lastIncome", fii.lastIncome)
                .set("nextIncome", fii.nextIncome)

            mongo.updateFirst(query, update, Fii::class.java).modifiedCount.right()     // It will return > 0 if something was really changed from the previous data
        }.getOrElse {
            ServiceErrors.InternalError(it.message ?: "Error in upserting data in database: ${it.localizedMessage}").left()
        }

}
