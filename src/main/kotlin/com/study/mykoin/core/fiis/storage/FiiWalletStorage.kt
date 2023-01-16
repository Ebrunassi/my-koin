package com.study.mykoin.core.fiis.storage

import arrow.core.Either
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.domain.fiis.Fii

sealed interface FiiWalletStorage {
    fun save(fii: Fii): Either<ServiceErrors, Fii>
    fun findById(id: Long): Fii?
    fun findByUserId(userId: Long): List<Fii>?
    fun findByName(name: String): Either<ServiceErrors, Fii?>
    fun upsert(fii: Fii): Either<ServiceErrors, Long>
}
