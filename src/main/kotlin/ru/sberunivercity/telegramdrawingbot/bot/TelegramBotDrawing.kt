package ru.sberunivercity.telegramdrawingbot.bot

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Service
class TelegramBotDrawing: TelegramLongPollingBot() {

    @Value("\${telegram.botName}")
    private lateinit var botName: String

    @Value("\${telegram.token}")
    private lateinit var token: String
    override fun getBotUsername(): String = botName
    override fun getBotToken(): String = token


    @PostConstruct
    fun initBot() {
        val botApi = TelegramBotsApi(DefaultBotSession::class.java)
        botApi.registerBot(this)
    }

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
