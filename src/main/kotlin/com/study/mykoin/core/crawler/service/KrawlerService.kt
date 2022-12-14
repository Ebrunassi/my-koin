package com.study.mykoin.core.crawler.service

import org.springframework.stereotype.Component

interface KrawlerService {
    fun handle(url: String, data: String?)
}