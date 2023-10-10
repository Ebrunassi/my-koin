package com.study.mykoin.ports.outbound

import arrow.core.Either
import com.study.mykoin.domain.fiis.fii.FiiAggregate
import com.study.mykoin.domain.fiis.fii.FiiId
import com.study.mykoin.domain.fiis.profile.ProfileId
import com.study.mykoin.domain.fiis.wallet.Wallet
import com.study.mykoin.domain.fiis.wallet.WalletId
import com.study.mykoin.usecases.ServiceErrors
import org.springframework.data.mongodb.repository.MongoRepository

interface WalletStorage {

    fun save(wallet: Wallet): Either<ServiceErrors, Wallet>

    fun findByFiiId(id: WalletId): Either<ServiceErrors, Wallet>

    fun findAggregateIdByFiiId(walletId: WalletId, id: String): FiiAggregate?

    fun updateFiiAggregate(fiiAggregate: FiiAggregate, fiiId: String): Either<ServiceErrors, Long>
}