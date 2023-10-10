package com.study.mykoin.domain.fiis.wallet

import com.study.mykoin.domain.fiis.fii.FiiAggregate
import kotlinx.serialization.Serializable
import org.springframework.data.mongodb.core.mapping.Document

/**
 * RootAggregate
 * This class represents a connection a specific Profile have with a FII.
 * It will hold information like:
 * - quantity
 * - total invested
 * - etc
 */
@Document(collection = "wallet")
@Serializable
class Wallet (
    val id: String,
    val profileId: String,
    val owningFiis: List<String>,       // FiiAggregate id
    val yield: Double
){
    companion object {
        fun create(profileId: String) =
            Wallet(
                id = WalletId.new().stringValue(),
                profileId = profileId,
                owningFiis = emptyList(),
                yield = 0.0
            )
    }
}