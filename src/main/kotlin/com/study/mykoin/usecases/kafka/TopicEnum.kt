package com.study.mykoin.usecases.kafka

enum class TopicEnum(val topicName: String) {
    FIIS_HISTORY_TOPIC("fiis_history_topic"),
    FIIS_WALLET_TOPIC("fiis_wallet_topic"),
    FIIS_TOPIC("fiis_topic"),
    USER_PROFILE_TOPIC("profile_topic"),
    DOMAIN_EVENTS_TOPIC("domain_events_topic")
}
