package com.study.mykoin.core.common.storage

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.FindAndModifyOptions.options
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import java.util.*

@Component
class SequenceGenerator {
    @Autowired
    private lateinit var mongoOperations: MongoOperations

    fun genereteSequence(sequenceName: String): Long {
        val counter: DatabaseSequence? = mongoOperations.findAndModify(
            query(where("_id").`is`(sequenceName)),
            Update().inc("seq", 1), options().returnNew(true).upsert(true),
            DatabaseSequence::class.java
        )
        return counter?.seq ?: 1
    }
}