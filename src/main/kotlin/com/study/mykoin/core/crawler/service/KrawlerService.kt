package com.study.mykoin.core.crawler.service

import io.thelandscape.krawler.http.KrawlDocument
import org.springframework.stereotype.Component

interface KrawlerService {
    fun handle(url: String, data: KrawlDocument?)
}