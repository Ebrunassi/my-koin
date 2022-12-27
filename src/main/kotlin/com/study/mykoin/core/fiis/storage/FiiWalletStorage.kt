package com.study.mykoin.core.fiis.storage

import com.study.mykoin.domain.fiis.Fii

sealed interface FiiWalletStorage {
    fun save(fii: Fii): Fii
    fun findById(id: Long): Fii?
    fun findByUserId(userId: Long): List<Fii>?
    fun findByName(name: String): Fii?
    fun upsert(fii: Fii): Long
}