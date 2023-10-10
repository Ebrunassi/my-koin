package com.study.mykoin.usecases.crawler.service

import io.thelandscape.krawler.http.KrawlDocument

interface KrawlerService {
    fun handle(url: String, data: KrawlDocument?)
}
