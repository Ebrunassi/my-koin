package com.study.mykoin.core.crawler.service

import io.thelandscape.krawler.http.KrawlDocument

interface KrawlerService {
    fun handle(url: String, data: KrawlDocument?)
}
