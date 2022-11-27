package com.study.mykoin.core.fiis.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EntryService {
    private val logger = LoggerFactory.getLogger("EntryService")


    fun handler(record: String){
        logger.info("TODO.. handle with the received entry -> $record")
    }
}