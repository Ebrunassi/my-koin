package com.study.mykoin.core.fiis.storage

import arrow.core.Either
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.domain.fiis.MonthIncome
import com.study.mykoin.domain.fiis.Profile

interface ProfileStorage {
    fun save(profile: Profile): Either<Exception, Profile>
    fun findById(id: Long): Either<ServiceErrors, Profile>
    fun findByName(username: String): Profile?
    fun upsertFiiWallet(userId: Long, fiiWalletId: Long): Either<ServiceErrors, Long>
    fun createMonthIncome(userId: Long, monthIncome: MonthIncome): Either<ServiceErrors, Long>
    fun updateMonthIncome(userId: Long, newMonthIncome: MutableSet<MonthIncome>): Either<ServiceErrors, Long>
    fun listAll(): Either<ServiceErrors, List<Profile>>
}
