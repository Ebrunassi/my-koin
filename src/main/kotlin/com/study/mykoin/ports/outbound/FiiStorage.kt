package com.study.mykoin.ports.outbound

import arrow.core.Either
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.domain.fiis.fii.Fii

interface FiiStorage {
    fun save(fii: Fii): Either<ServiceErrors, Fii>
    fun findById(id: Long): Fii?
    fun findByUserId(userId: Long): List<Fii>?

    fun findAll(): List<Fii>?
    fun findByName(name: String): Either<ServiceErrors, Fii?>
    fun upsert(fii: Fii): Either<ServiceErrors, Long>
}
