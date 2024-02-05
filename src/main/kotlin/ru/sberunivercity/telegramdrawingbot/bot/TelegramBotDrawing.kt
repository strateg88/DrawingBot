package ru.sberunivercity.telegramdrawingbot.bot

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.sql.DriverManager


class TelegramBotDrawing : TelegramLongPollingBot() {

    override fun getBotUsername(): String {
        return "DrawingKotlinBot"
    }

    override fun getBotToken(): String {
        return "6716074031:AAEgZdJxFQlemtVbzxAI667r_rerWfzge3Q"
    }


    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val message = update.message
            val chatId = message.chatId
            val text = message.text
            println("Пользователь $chatId написал: $text")
            if (text.startsWith("/")) {
                if (text == "/start") {
                    sendMessage(
                        chatId,
                        "Привет! Я бот для проведения розыгрышей. Для участия, отправь свое имя и фамилию."
                    )
                }
            } else {
                // Мой id = 245599936L - я админ
                if (245599936L == chatId) {
                    //тут отправлять админскую клавиатуру
                    val markupInline = InlineKeyboardMarkup()
                    // добавляем встроенную клавиатуру в сообщение
                    val rowsInline: MutableList<List<InlineKeyboardButton>> = ArrayList()
                    val rowInline1: MutableList<InlineKeyboardButton> = ArrayList()

                    val inlineKeyboardButton1 = InlineKeyboardButton()
                    inlineKeyboardButton1.text = "Розыграть!";
                    inlineKeyboardButton1.callbackData = "CB Розыграть!";
                    rowInline1.add(inlineKeyboardButton1);
                    rowsInline.add(rowInline1)

                    markupInline.keyboard = rowsInline
                    message.replyMarkup = markupInline
                    sendMessage(chatId, markupInline)
                } else {
                    try {
                        addToDatabase(chatId, text)
                    } catch (exc: org.postgresql.util.PSQLException) {
                        println(exc.message)
                        if (exc.message != null && exc.message!!.contains("ОШИБКА: повторяющееся значение ключа нарушает ограничение уникальности", true)) {
                            sendMessage(chatId, "Вы уже зарегистрированы!")
                            return
                        }
                    }
                    sendMessage(chatId, "Вы успешно зарегистрированы для участия в розыгрыше!")
                }
            }
        }
    }

    fun sendMessage(chatId: Long, text: String) {
        val message = SendMessage()
        message.chatId = chatId.toString()
        message.text = text
        try {
            execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun sendMessage(chatId: Long, keyboard: InlineKeyboardMarkup) {
        try {
            val message = SendMessage()
            message.chatId = chatId.toString()
            message.replyMarkup = keyboard
            message.text = "Клавиатура"
            execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun runLottery() {
        val connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "q12345678")
        val statement = connection.prepareStatement("SELECT chat_id FROM participant")
        val resultSet = statement.executeQuery()
        val chatIds = mutableListOf<Long>()
        while (resultSet.next()) {
            chatIds.add(resultSet.getLong("chat_id"))
        }
        connection.close()

        val winnerChatId = chatIds.random()
        sendMessage(winnerChatId, "Поздравляем! Вы стали победителем розыгрыша!")
    }
}



fun addToDatabase(chatId: Long, name: String) {
    val connection =
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "q12345678")
    val statement = connection.prepareStatement("INSERT INTO participant (chat_id, name) VALUES (?, ?)")
    statement.setLong(1, chatId)
    statement.setString(2, name)
    statement.executeUpdate()
    connection.close()
}
fun main() {
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    val telegramBotDrawing = TelegramBotDrawing()
    botsApi.registerBot(telegramBotDrawing)
    println("Started.")

    Thread.sleep(10 * 60 * 1000)
    telegramBotDrawing.runLottery()
}

//override fun onUpdateReceived(update: Update) {
//
//    if (update.hasMessage() && update.message.hasText()) {
//        val message = update.message
//        val chatId = message.chatId
//        val text = message.text
//    }
//}
