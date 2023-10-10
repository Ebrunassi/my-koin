package com.study.mykoin.ports.outbound

import arrow.core.Either
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.domain.fiis.FiiEntry

interface FiiHistoryStorage {
    fun save(entry: FiiEntry): Either<ServiceErrors, FiiEntry>
}
