package com.study.mykoin.domain.fiis

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "fiis")
data class FiiEntry (
    @Id
    var id: Long,
    val name: String,
    val quantity: Int,
    val averagePrice: Double,
    val totalInvested: Double,
    val type: String
    ){

    companion object {
        @Transient
        val SEQUENCE_NAME = "fiis_sequence"
    }

}