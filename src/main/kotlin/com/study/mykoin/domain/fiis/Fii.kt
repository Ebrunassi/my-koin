package com.study.mykoin.domain.fiis

import com.study.mykoin.core.fiis.model.enums.CurrencyEnum
import com.study.mykoin.core.fiis.model.enums.FiiTypeEnum
import com.study.mykoin.core.fiis.model.enums.ResourceTypeEnum
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * This domain is used in FiiWallet collection.
 * It holds all the information regarding the Fii that I have in my wallet
 */
@Document(collection = "fiis")
class Fii (
    @Id
    var id: Long?,
    val name: String,
    var quantity: Int,
    val type: FiiTypeEnum,
    val resourceType: ResourceTypeEnum?,
    var averagePrice: Double,
    var totalInvested: Double,
    val currency: CurrencyEnum?,
    var monthlyIncome: Double,
    var porcent: Double
){
    companion object{
        @Transient
        val SEQUENCE_NAME = "FIIS-SEQUENCE"
    }
}

fun Fii.updateFii(updatedFii: FiiEntry) {
    quantity += updatedFii.quantity
    totalInvested += updatedFii.totalInvested
    averagePrice = totalInvested / quantity
}