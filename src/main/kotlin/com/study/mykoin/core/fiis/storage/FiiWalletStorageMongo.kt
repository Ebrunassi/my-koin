package com.study.mykoin.core.fiis.storage

import arrow.core.right
import com.study.mykoin.core.common.storage.SequenceGenerator
import com.study.mykoin.domain.fiis.Fii
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.update
import org.springframework.stereotype.Repository

@Repository
class FiiWalletStorageMongo: FiiWalletStorage {
    private val logger = LoggerFactory.getLogger(FiiHistoryStorageMongo::class.java)
    @Autowired
    private lateinit var mongo: MongoOperations
    @Autowired
    private lateinit var sequenceGenerator: SequenceGenerator
    override fun save(fii: Fii): Fii {
        fii.id = sequenceGenerator.genereteSequence(Fii.SEQUENCE_NAME)
        logger.info("Saving $fii in database")
        return mongo.save(fii).also { logger.info("Saved successfully") }
    }

    override fun findById(id: Long): Fii? {
        return mongo.findById(id, Fii::class.java)
    }

    override fun findByName(name: String): Fii? {
        val query = Query(Criteria.where("name").`is`(name))
        return mongo.find(query, Fii::class.java).firstOrNull()
    }

    override fun upsert(fii: Fii): Long {
        val query = Query(Criteria.where("name").`is`(fii.name))
        val update = Update()
            .set("quantity", fii.quantity)
            .set("totalInvested", fii.totalInvested)
            .set("averagePrice", fii.averagePrice)
            .set("monthlyIncome", fii.monthlyIncome)        // TODO - Probably we will need to set new values for other attributes too
            .set("lastIncome", fii.lastIncome)
        return mongo.updateFirst(query, update, Fii::class.java).modifiedCount
    }
}