package com.study.mykoin.usecases

import org.springframework.http.HttpStatus
import java.util.*

/**
 * This interface contains all the possible error in the system
 * Please add any other error you see that could happen
 */
sealed interface ServiceErrors {
    data class BadRequest(val reason: String) : ServiceErrors {
        val status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    data class HandlerError(val reason: String) : ServiceErrors {
        val uuid = UUID.randomUUID().toString()
    }

    data class InternalError(val reason: String) : ServiceErrors {
        val uuid = UUID.randomUUID().toString()
    }

    data class FiiWalletNotFound(val reason: String) : ServiceErrors {
        val uuid = UUID.randomUUID().toString()
    }

    @Deprecated("Use EntityNotFound instead")
    data class ProfileNotFound(val reason: String) : ServiceErrors {
        val uuid = UUID.randomUUID().toString()
    }

    data class EntityNotFound(val reason: String) : ServiceErrors {
        val uuid = UUID.randomUUID().toString()
    }

    data class AlreadyExistingProfile(val reason: String) : ServiceErrors {
        val uuid = UUID.randomUUID().toString()
    }

    data class InvalidRequestBody(val reason: String) : ServiceErrors {
        val uuid = UUID.randomUUID().toString()
    }

    data class InvalidArgument(val reason: String) : ServiceErrors {
        val uuid = UUID.randomUUID().toString()
    }

    data class InvalidUuid(val reason: String) : ServiceErrors {
        val uuid = UUID.randomUUID().toString()
    }
}
