package com.study.mykoin.helper

class Functions {}

/**
 * Runs this function if the receiver is null
 */
fun <T> T?.otherwise(fn: () -> T): T {
    return this ?: fn()
}