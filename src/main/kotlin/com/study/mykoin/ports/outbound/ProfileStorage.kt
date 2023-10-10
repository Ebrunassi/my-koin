package com.study.mykoin.ports.outbound

import arrow.core.Either
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.domain.fiis.MonthIncome
import com.study.mykoin.domain.fiis.profile.Profile
import com.study.mykoin.domain.fiis.profile.ProfileId
import java.util.UUID

interface ProfileStorage {
    fun save(profile: Profile): Either<Exception, Profile>
    fun findById(id: ProfileId): Either<ServiceErrors, Profile>
    fun findByName(username: String): Profile?
    fun upsertFiiWallet(userId: Long, fiiWalletId: Long): Either<ServiceErrors, Long>
    fun createMonthIncome(userId: Long, monthIncome: MonthIncome): Either<ServiceErrors, Long>
    fun updateMonthIncome(userId: Long, newMonthIncome: MutableSet<MonthIncome>): Either<ServiceErrors, Long>
    fun listAll(): Either<ServiceErrors, List<Profile>>
}
