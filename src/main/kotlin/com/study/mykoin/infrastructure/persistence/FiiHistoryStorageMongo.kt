package com.study.mykoin.infrastructure.persistence

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.domain.fiis.FiiEntry
import com.study.mykoin.ports.outbound.FiiHistoryStorage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Repository

@Repository
class FiiHistoryStorageMongo : FiiHistoryStorage {
    private val logger = LoggerFactory.getLogger(FiiHistoryStorageMongo::class.java)
    @Autowired
    private lateinit var mongo: MongoOperations
    @Autowired
    private lateinit var sequenceGenerator: SequenceGenerator

    override fun save(entry: FiiEntry): Either<ServiceErrors, FiiEntry> {
        return try {
            entry.id = sequenceGenerator.genereteSequence(FiiEntry.SEQUENCE_NAME)
            logger.info("Saving $entry in database")
            mongo.save(entry).right().also { logger.info("Saved successfully") }
        } catch (e: Exception) {
            ServiceErrors.InternalError(e.message ?: "Error in saving the data in database: ${e.localizedMessage}").left()
        }
    }
}
