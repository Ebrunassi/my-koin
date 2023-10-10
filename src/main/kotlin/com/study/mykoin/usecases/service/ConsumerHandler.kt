package com.study.mykoin.usecases.service

interface ConsumerHandler {
    fun handler(key: String, record: String)
}
