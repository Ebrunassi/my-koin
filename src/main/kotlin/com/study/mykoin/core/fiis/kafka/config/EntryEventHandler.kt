package com.study.mykoin.core.fiis.kafka.config

import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EntryEventHandler(val kafkaProducer: KafkaProducer<String, String>, val topic: String): EventHandler {

    private val log : Logger = LoggerFactory.getLogger(EntryEventHandler::class.qualifiedName)

    override fun onOpen() {
        TODO("Not yet implemented")
    }

    override fun onClosed() {
        TODO("Not yet implemented")
    }

    override fun onMessage(event: String?, messageEvent: MessageEvent?) {
        log.info("Sending a new message: '${messageEvent!!.data}'")
        kafkaProducer.send(ProducerRecord<String, String> (topic, messageEvent.data))
    }

    override fun onComment(comment: String?) {
        TODO("Not yet implemented")
    }

    override fun onError(t: Throwable?) {
        TODO("Not yet implemented")
    }
}