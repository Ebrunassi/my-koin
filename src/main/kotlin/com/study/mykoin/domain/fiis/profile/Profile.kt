package com.study.mykoin.domain.fiis.profile

import com.study.mykoin.domain.fiis.MonthIncome
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

/**
 * RootAggregate
 */
@Document(collection = "profile")
@Serializable
class Profile(
    @Id
    var id: String,
    val username: String,
    val password: String,
    var monthIncome: Collection<MonthIncome> = emptyList(), // Regarding all the incomes this user has
    var totalInvested: Double,
) {
    companion object {
        const val SEQUENCE_NAME = "profile-sequence"

        fun create(
            username: String,
            password: String
        ) =
            Profile(
                id = ProfileId.new().value.toString(),
                username = username,
                password =  password,
                totalInvested =  0.toDouble()
            )
    }
}
