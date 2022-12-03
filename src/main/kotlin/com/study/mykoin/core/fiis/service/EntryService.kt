package com.study.mykoin.core.fiis.service

import com.google.gson.Gson
import com.study.mykoin.core.fiis.model.FiiDTO
import com.study.mykoin.core.fiis.storage.FiiStorage
import com.study.mykoin.domain.fiis.FiiEntry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EntryService {
    private val logger = LoggerFactory.getLogger("EntryService")

    @Autowired
    private lateinit var fiiRepository: FiiStorage

    fun handler(record: String){
        logger.info("Received new message: ${record}")
        try {
            fiiRepository.save(record.mapToFiiEntity())
        } catch(e: Exception) {
            logger.error(e.message)
            e.printStackTrace()
        }
    }
}

fun String.mapToFiiEntity(): FiiEntry {
    return Gson().fromJson(this, FiiEntry::class.java)
}