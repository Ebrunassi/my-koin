package com.study.mykoin.core.kafka

enum class ConsumerGroupEnum(val groupId: String) {
    FII_ENTRY_GROUP("FII_ENTRY_GROUP"),
    FII_WALLET_GROUP("FII_WALLET_GROUP"),
    USER_PROFILE_GROUP("USER_PROFILE_GROUP")
}