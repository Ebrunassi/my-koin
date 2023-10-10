package com.study.mykoin.domain.fiis.fii

import kotlinx.serialization.Serializable
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "fii-aggregate")
@Serializable
data class FiiAggregate(
    val id: String,
    val fiiId: String,
    val walletId: String,
    val quantity: Int,
    val totalInvested: Double,
    val averagePrice: Double,
    val porcent: Double
) {

    companion object {
        fun new(
            fiiId: String,
            walletId: String,
            quantity: Int,
            price: Double
        ) =
            FiiAggregate(
                id = FiiId.new().stringValue(),
                fiiId = fiiId,
                walletId = walletId,
                quantity = quantity,
                totalInvested = price * quantity,
                averagePrice = price,
                porcent = 0.0
            )
    }

    // TODO - Double check this function!
    fun update(amount: Int, price: Double) =
        this.copy(
            quantity = quantity + amount,
            totalInvested = amount * price + totalInvested,
            averagePrice = (amount * price + totalInvested) / (quantity + amount)
        )

}