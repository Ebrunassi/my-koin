package com.study.mykoin.core.fiis.storage

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.common.storage.SequenceGenerator
import com.study.mykoin.domain.fiis.MonthIncome
import com.study.mykoin.domain.fiis.Profile
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.updateFirst
import org.springframework.stereotype.Repository

@Repository
class ProfileStorageMongo : ProfileStorage {
    private val logger = LoggerFactory.getLogger(FiiHistoryStorageMongo::class.java)
    @Autowired
    private lateinit var mongo: MongoOperations
    @Autowired
    private lateinit var sequenceGenerator: SequenceGenerator

    override fun save(profile: Profile): Either<Exception, Profile> {
        try {
            profile.id = sequenceGenerator.genereteSequence(Profile.SEQUENCE_NAME)
            logger.info("Saving $profile in database")
            return mongo.save(profile).also { logger.info("Profile '${profile.username}' saved successfully") }.right()
        } catch (e: Exception) {
            return e.left()
        }
    }

    override fun findById(id: Long): Either<ServiceErrors, Profile> {
        val profile = mongo.findById(id, Profile::class.java)
        return profile?.right() ?: ServiceErrors.ProfileNotFound("Profile has not been found").left()
    }

    override fun findByName(username: String): Profile? {
        val query = Query(Criteria.where("username").`is`(username))
        return mongo.find(query, Profile::class.java).firstOrNull()
    }

    override fun upsertFiiWallet(userId: Long, fiiWalletId: Long): Either<ServiceErrors, Long> {
        return try {
            val query = Query(Criteria.where("id").`is`(userId))
            val update = Update()
                .addToSet("fiiWallet", fiiWalletId)
            mongo.updateFirst(query, update, Profile::class.java).modifiedCount.right()
        } catch (e: Exception) {
            ServiceErrors.InternalError(e.message ?: "Error in upserting data in database: ${e.localizedMessage}").left()
        }
    }

    override fun createMonthIncome(userId: Long, monthIncome: MonthIncome): Either<ServiceErrors, Long> =
        either.runCatching {
            val query = Query(Criteria.where("id").`is`(userId))
            val update = Update()
                .addToSet("monthIncome", monthIncome)
            mongo.updateFirst(query, update, Profile::class.java).modifiedCount.right()
        }.getOrElse {
            ServiceErrors.InternalError(it.message ?: "Error in upserting data in database: ${it.localizedMessage}").left()
        }

    override fun updateMonthIncome(userId: Long, newMonthIncome: MutableSet<MonthIncome>): Either<ServiceErrors, Long> =
        either.runCatching {
            val query = Query(Criteria.where("id").`is`(userId))
            val update = Update().set("monthIncome", newMonthIncome)
            mongo.updateFirst(query, update, Profile::class.java).modifiedCount.right()
        }.getOrElse {
            ServiceErrors.InternalError(it.message ?: "Error in upserting data in database: ${it.localizedMessage}").left()
        }

    override fun listAll(): Either<ServiceErrors, List<Profile>> =
        either.runCatching {
            mongo.findAll(Profile::class.java).right()
        }.getOrElse {
            ServiceErrors.InternalError("Something wrong happened when listing all the profiles: ${it.message}").left()
        }

}
