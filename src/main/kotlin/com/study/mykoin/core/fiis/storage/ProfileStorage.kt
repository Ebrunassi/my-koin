package com.study.mykoin.core.fiis.storage

import com.study.mykoin.domain.fiis.Fii
import com.study.mykoin.domain.fiis.Profile

interface ProfileStorage {
    fun save(profile: Profile): Profile
    fun findById(id: Long): Profile?

    fun findByName(username: String): Profile?

    fun upsert(userId: Long, fiiWalletId: Long): Long
}