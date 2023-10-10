package com.study.mykoin.usecases.service

import arrow.core.Either
import arrow.core.computations.ResultEffect.bind
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import arrow.core.rightIfNull
import com.study.mykoin.domain.fiis.profile.Profile
import com.study.mykoin.domain.fiis.wallet.Wallet
import com.study.mykoin.helper.leftIfNull
import com.study.mykoin.ports.inbound.ProfileService
import com.study.mykoin.ports.outbound.ProfileStorage
import com.study.mykoin.ports.outbound.WalletStorage
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.usecases.service.commands.ProfileCommand
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
    private val profileStorage: ProfileStorage,
    private val walletStorage: WalletStorage
): ProfileService {
    override fun create(profileCommand: ProfileCommand): Either<ServiceErrors, Profile> =
        profileStorage.findByName(profileCommand.username.stringValue())
            .rightIfNull { ServiceErrors.AlreadyExistingProfile("There is already a profile with this username " +
                    "'${profileCommand.username.stringValue()}'") }
            .map { Profile.create(profileCommand.username.stringValue(), profileCommand.password.stringValue()) }
            .map {
                profileStorage.save(it).bind().also { walletStorage.save(Wallet.create(it.id)) }
            }
            // TODO - create a DomainEvent to create a new wallet for the user
        }