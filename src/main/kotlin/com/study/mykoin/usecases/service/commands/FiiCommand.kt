package com.study.mykoin.usecases.service.commands

import com.study.mykoin.domain.fiis.fii.FiiName
import com.study.mykoin.domain.fiis.fii.FiiTypeEnum
import com.study.mykoin.domain.fiis.wallet.WalletId
import com.study.mykoin.domain.fiis.profile.ProfileId

/**
 * Fii entry. This object encapsulate the fii from http request
 */
class FiiCommand(
    val name: FiiName,
    val userId: ProfileId,
    val walletId: WalletId,
    val quantity: Int,
    val price: Double,
    var transactionDate: String?, // TODO - Create an object to hold this value and also implement logic on it
    var totalInvested: Double,
    val type: FiiTypeEnum
){
}