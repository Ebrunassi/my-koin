package com.study.mykoin.domain.fiis

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "fiis-history")
data class FiiEntry (
    @Id
    var id: Long,
    val name: String,
    val quantity: Int,
    val averagePrice: Double,
    val totalInvested: Double,
    var transactionDate: String,        // TODO - Create an object to hold this value and also implement logic on it
    val type: String
    ){

    companion object {
        @Transient
        val SEQUENCE_NAME = "fiis-history-sequence"
    }

}