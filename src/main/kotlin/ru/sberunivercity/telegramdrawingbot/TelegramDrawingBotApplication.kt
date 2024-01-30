package ru.sberunivercity.telegramdrawingbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TelegramDrawingBotApplication

fun main(args: Array<String>) {
	runApplication<TelegramDrawingBotApplication>(*args)
}
