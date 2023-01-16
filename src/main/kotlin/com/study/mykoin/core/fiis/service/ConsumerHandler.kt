package com.study.mykoin.core.fiis.service

sealed interface ConsumerHandler {
    fun handler(key: String, record: String)
}