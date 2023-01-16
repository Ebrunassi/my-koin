package com.study.mykoin.core.fiis.storage

import arrow.core.Either
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.domain.fiis.FiiEntry

interface FiiHistoryStorage {
    fun save(entry: FiiEntry): Either<ServiceErrors, FiiEntry>
}
