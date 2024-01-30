package ru.sberunivercity.telegramdrawingbot.bot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.ZoneId


@Service
class TelegramBotDrawing: TelegramLongPollingBot() {

    @Value("\${telegram.botName}")
    private val botName: String = ""

    @Value("\${telegram.token}")
    private val token: String = ""
    override fun getBotUsername(): String = botName
    override fun getBotToken(): String = token

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()){
            val message = update.message
            val chatId = message.chatId
            val responseText = if (message.hasText()) {
                val messageText = message.text
                when {
                    messageText == "/start" -> "Добро пожаловать!"
                    else -> "Вы написали: *$messageText*"}
                } else {
                    "Я понимаю только текст"
                }
                sendNotification(chatId, responseText)
            }
        }
        private fun sendNotification(chatId: Long, responseText: String){
            val responseMessage = SendMessage(chatId.toString(), responseText)
            responseMessage.enableMarkdownV2(true)
            execute(responseMessage)
        }
    }
