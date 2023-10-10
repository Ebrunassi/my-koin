package com.study.mykoin.adapters.http.api

import arrow.core.continuations.either
import arrow.core.flatMap
import com.study.mykoin.adapters.http.api.dtos.ServiceResponse
import com.study.mykoin.adapters.http.api.dtos.ProfileDTO
import com.study.mykoin.adapters.http.api.dtos.mappers.ProfileRequestMapper
import com.study.mykoin.helper.eitherBody
import com.study.mykoin.helper.handleCall
import com.study.mykoin.ports.inbound.ProfileService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class ProfileController(
    private val profileService: ProfileService
) {

    suspend fun createProfile(request: ServerRequest): ServerResponse =
        request.eitherBody<ProfileDTO>()
            .flatMap { createProfile ->
                either { ProfileRequestMapper.mapToCreateProfileCommand(createProfile).bind() }
            }.flatMap {
                profileService.create(it).map {
                    ServiceResponse.EntryCreated("The profile ${it.username} has been created")
                }
            }
    .handleCall()
}
