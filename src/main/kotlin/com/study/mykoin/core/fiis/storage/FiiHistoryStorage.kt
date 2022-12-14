package com.study.mykoin.core.fiis.storage

import com.study.mykoin.domain.fiis.FiiEntry

interface FiiHistoryStorage {
    fun save(entry: FiiEntry)
}