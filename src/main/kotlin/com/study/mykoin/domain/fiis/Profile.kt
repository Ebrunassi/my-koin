package com.study.mykoin.domain.fiis

import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "profile")
@Serializable
class Profile(
    @Id
    var id: Long?,
    val username: String,
    var password: String,
    var monthIncome: Double, // Regarding all the incomes this user has
    var totalInvested: Double,
    var fiiWallet: Collection<Long> = emptyList()
) {
    companion object {
        const val SEQUENCE_NAME = "profile-sequence"
    }
}
