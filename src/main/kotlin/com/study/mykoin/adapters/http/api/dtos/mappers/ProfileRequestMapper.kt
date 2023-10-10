package com.study.mykoin.adapters.http.api.dtos.mappers

import arrow.core.continuations.either
import com.study.mykoin.adapters.http.api.dtos.ProfileDTO
import com.study.mykoin.domain.fiis.profile.Password
import com.study.mykoin.domain.fiis.profile.Username
import com.study.mykoin.usecases.service.commands.ProfileCommand

object ProfileRequestMapper {
    fun mapToCreateProfileCommand(createProfileDTO: ProfileDTO) =
        either.eager {
            ProfileCommand(
                Username.of(createProfileDTO.username).bind(),
                Password.of(createProfileDTO.password).bind(),
            )
    }
}