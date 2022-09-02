package com.study.mykoin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MyKoinApplication

fun main(args: Array<String>) {
	runApplication<MyKoinApplication>(*args)
}
