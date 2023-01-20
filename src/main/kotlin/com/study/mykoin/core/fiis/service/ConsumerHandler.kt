package com.study.mykoin.core.fiis.service

interface ConsumerHandler {
    fun handler(key: String, record: String)
}