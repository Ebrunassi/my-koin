package com.study.mykoin.domain.fiis.events

enum class EventType(val eventType: String) {

    /**
     * If fii got new information from crawler, we must update profile information
     */
    FII_INFORMATION_UPDATED("FII_INFORMATION_UPDATED"),

    /**
     * Ask for an update in profile monthIncome.
     * An update means that all the information regarding monthIncome
     * must be reprocessed because probably we got a new value which will impact update the 'value' and 'yield'
     * we have already got.
     */
    PROFILE_UPDATE_MONTHINCOME("PROFILE_UPDATE_MONTHINCOME")
}
