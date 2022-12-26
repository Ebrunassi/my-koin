package com.study.mykoin.core.kafka

enum class TopicEnum(val topicName: String) {
    FIIS_HISTORY_TOPIC("fiis_history_topic"),
    FIIS_WALLET_TOPIC("fiis_wallet_topic"),
    USER_PROFILE_TOPIC("profile_topic")
}