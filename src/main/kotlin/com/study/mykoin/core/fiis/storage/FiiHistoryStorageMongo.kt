package com.study.mykoin.core.fiis.storage

import com.study.mykoin.core.common.storage.SequenceGenerator
import com.study.mykoin.domain.fiis.FiiEntry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Repository

@Repository
class FiiHistoryStorageMongo: FiiHistoryStorage{
    private val logger = LoggerFactory.getLogger(FiiHistoryStorageMongo::class.java)
    @Autowired
    private lateinit var mongo: MongoOperations
    @Autowired
    private lateinit var sequenceGenerator: SequenceGenerator

    override fun save(entry: FiiEntry) {
        entry.id = sequenceGenerator.genereteSequence(FiiEntry.SEQUENCE_NAME)
        logger.info("Saving $entry in database")
        mongo.save(entry)
        logger.info("Saved successfully")
    }

}