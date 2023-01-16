package com.study.mykoin.core.common.response

import kotlinx.serialization.Serializable
import org.springframework.http.HttpStatus

/**
 * This interface contains all the possible response in the system
 * Please add any other response data class you see that could be exists
 */
sealed interface ServiceResponse {

    @Serializable
    data class HelloMyKoin(val greetings: String)
    @Serializable
    data class EventSubmited(val message: String) {
        val statusCode: HttpStatus = HttpStatus.CREATED
    }
}
