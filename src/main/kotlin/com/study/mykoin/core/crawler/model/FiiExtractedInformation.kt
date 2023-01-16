package com.study.mykoin.core.crawler.model

class FiiExtractedInformation(
    val name: String,
) {
    var lastIncome: LastIncome = LastIncome()
    var nextIncome: NextIncome = NextIncome()
}
