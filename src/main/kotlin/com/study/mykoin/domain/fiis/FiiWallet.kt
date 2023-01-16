package com.study.mykoin.domain.fiis

import com.study.mykoin.core.crawler.model.LastIncome
import com.study.mykoin.core.crawler.model.NextIncome
import com.study.mykoin.core.fiis.model.enums.CurrencyEnum
import com.study.mykoin.core.fiis.model.enums.FiiTypeEnum
import com.study.mykoin.core.fiis.model.enums.ResourceTypeEnum
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * This domain is used in FiiWallet collection.
 * It holds all the information regarding the Fii that I have in my wallet
 */
@Document(collection = "fiis-wallet")
@Serializable
class Fii(
    @Id
    var id: Long?,
    val name: String,
    var quantity: Int,
    val userId: Long,
    val type: FiiTypeEnum,
    val resourceType: ResourceTypeEnum?,
    var averagePrice: Double,
    var totalInvested: Double,
    val currency: CurrencyEnum?,
    var monthlyIncome: Double, // Regarding only to this fii
    var lastIncome: LastIncome = LastIncome(),
    var nextIncome: NextIncome = NextIncome(),
    var porcent: Double
) {
    companion object {
        @Transient
        const val SEQUENCE_NAME = "fiis-wallet-sequence"
    }
}

fun Fii.updateFii(updatedFii: FiiEntry) {
    quantity += updatedFii.quantity
    totalInvested += updatedFii.totalInvested
    averagePrice = totalInvested / quantity
}
