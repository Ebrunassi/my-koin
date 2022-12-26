package com.study.mykoin.core.common.errors

import org.springframework.http.HttpStatus
import java.util.*

/**
 * This interface contains all the possible error in the system
 * Please add any other error you see that could happen
 */
sealed interface ServiceErrors {
    data class BadRequest(val reason: String): ServiceErrors {
        val status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    data class HandlerError(val reason: String): ServiceErrors {
        val uuid = UUID.randomUUID().toString()
    }
}