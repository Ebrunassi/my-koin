package com.study.mykoin.ports.inbound

import arrow.core.Either
import com.study.mykoin.domain.fiis.profile.Profile
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.usecases.service.commands.ProfileCommand

interface ProfileService {
    fun create(profileCommand: ProfileCommand): Either<ServiceErrors, Profile>
}