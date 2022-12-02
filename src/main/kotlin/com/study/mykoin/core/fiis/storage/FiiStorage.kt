package com.study.mykoin.core.fiis.storage

import com.study.mykoin.domain.fiis.FiiEntry

interface FiiStorage {
    fun save(entry: FiiEntry)
}