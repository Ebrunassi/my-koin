package com.study.mykoin.core.fiis.storage

import com.study.mykoin.core.common.storage.SequenceGenerator
import com.study.mykoin.domain.fiis.Fii
import com.study.mykoin.domain.fiis.Profile
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class ProfileStorageMongo: ProfileStorage {
    private val logger = LoggerFactory.getLogger(FiiHistoryStorageMongo::class.java)
    @Autowired
    private lateinit var mongo: MongoOperations
    @Autowired
    private lateinit var sequenceGenerator: SequenceGenerator

    override fun save(profile: Profile): Profile {
        profile.id = sequenceGenerator.genereteSequence(Profile.SEQUENCE_NAME)
        logger.info("Saving $profile in database")
        return mongo.save(profile).also { logger.info("Saved successfully") }
    }

    override fun findById(id: Long): Profile? {
        TODO("Not yet implemented")
    }

    override fun findByName(username: String): Profile? {
        val query = Query(Criteria.where("username").`is`(username))
        return mongo.find(query, Profile::class.java).firstOrNull()
    }

    override fun upsert(username: String, fiiWalletId: Long): Long {
        val query = Query(Criteria.where("username").`is`(username))
        val update = Update()
            .addToSet("fiiWallet", fiiWalletId)
        return mongo.updateFirst(query, update, Profile::class.java).modifiedCount
    }
}