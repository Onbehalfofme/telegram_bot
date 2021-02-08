package ru.innopolis.telegram_bot.bot;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@Getter
public final class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String BOT_NAME;
    @Value("${bot.token}")
    private String BOT_TOKEN;
    private static final String CAPTION = "You would be beautiful in this world.";
    private static final String COMMON_MESSAGE = "Moya tvoya ne ponimat'";
    private static final String GREETINGS = "Hello %s, I can transform your selfie into 2d cartoon. Check it out. Just upload your selfie.";


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }


    @Override
    public void onUpdateReceived(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        if (update.getMessage().hasPhoto()) {
            List<String> fileIds = update.getMessage().getPhoto().stream().map(PhotoSize::getFileId).limit(1)
                    .collect(Collectors.toList());
            try {
                for (String fileId : fileIds) {
                    execute(SendPhoto.builder().chatId(chatId).photo(new InputFile(fileId)).caption(CAPTION).build());
                    log.info(update.getMessage().getChatId() + ": Photo is sent");
                }

            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        } else {
            if (update.getMessage().getText().equals("/start")) {
                try {
                    execute(SendMessage.builder().chatId(chatId).text(String.format(GREETINGS, update.getMessage().getFrom().getFirstName())).build());
                    log.info(update.getMessage().getChatId() + ": " + update.getMessage().getText());
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            } else {
                try {
                    execute(SendMessage.builder().chatId(chatId).text(COMMON_MESSAGE).build());
                    log.info(update.getMessage().getChatId() + ": " + update.getMessage().getText());
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }

        }

    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}