package com.study.mykoin.adapters.http.api.dtos.mappers

import arrow.core.continuations.either
import com.study.mykoin.adapters.http.api.dtos.FiiEntryDTO
import com.study.mykoin.domain.fiis.fii.FiiName
import com.study.mykoin.domain.fiis.fii.FiiTypeEnum
import com.study.mykoin.domain.fiis.wallet.WalletId
import com.study.mykoin.domain.fiis.profile.ProfileId
import com.study.mykoin.usecases.service.commands.FiiCommand

object FiiRequestMapper {
    fun mapToFiiCommand(fiiEntryDTO: FiiEntryDTO) = either.eager {
        FiiCommand(
            FiiName.of(fiiEntryDTO.name).bind(),
            ProfileId.of(fiiEntryDTO.userId).bind(),
            WalletId.of(fiiEntryDTO.walletId).bind(),
            fiiEntryDTO.quantity,
            fiiEntryDTO.price,
            fiiEntryDTO.transactionDate,
            fiiEntryDTO.totalInvested,
            FiiTypeEnum.valueOf(fiiEntryDTO.type)
        )
    }
}