package com.study.mykoin.ports.inbound

import arrow.core.Either
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.usecases.service.commands.FiiCommand

interface FiiService {
    suspend fun create(fiiCommand: FiiCommand): Either<ServiceErrors, Long>
}