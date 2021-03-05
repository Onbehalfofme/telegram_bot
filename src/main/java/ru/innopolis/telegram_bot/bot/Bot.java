package ru.innopolis.telegram_bot.bot;

import ai.djl.MalformedModelException;
import ai.djl.modality.cv.BufferedImageFactory;
import ai.djl.modality.cv.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.innopolis.telegram_bot.model.AnimeService;

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

    private final AnimeService animeService;

    public Bot(AnimeService animeService) {
        this.animeService = animeService;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }


    @Override
    public void onUpdateReceived(Update update) {
        String chatId = String.valueOf(update.getMessage().getChatId());
        if (update.getMessage().hasPhoto()) {
            List<java.io.File> files = update.getMessage().getPhoto().stream().map(this::getFilePath)
                    .map(this::downloadPhotoByFilePath).limit(1)
                    .collect(Collectors.toList());
            try {
                for (java.io.File file : files) {
                    BufferedImage image = ImageIO.read(file);
                    ByteArrayOutputStream os1 = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpeg", os1);
                    InputStream fis = new ByteArrayInputStream(os1.toByteArray());
                    Image im = new BufferedImageFactory()
                            .fromInputStream(fis);
                    Image result = animeService.transform(im);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    result.save(os, "jpeg");
                    InputStream is = new ByteArrayInputStream(os.toByteArray());
                    execute(SendPhoto.builder().chatId(chatId).photo(new InputFile(is, "result")).caption(CAPTION)
                                    .build());
                    log.info(update.getMessage().getChatId() + ": Photo is sent");
                }

            } catch (TelegramApiException | IOException | MalformedModelException e) {
                log.error(e.getMessage());
            }
        } else {
            if (update.getMessage().getText().equals("/start")) {
                try {
                    execute(SendMessage.builder().chatId(chatId)
                                    .text(String.format(GREETINGS, update.getMessage().getFrom().getFirstName()))
                                    .build());
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

    public java.io.File downloadPhotoByFilePath(String filePath) {
        try {
            return downloadFile(filePath);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getFilePath(PhotoSize photo) {
        Objects.requireNonNull(photo);

        if (photo.getFilePath() != null && !photo.getFilePath().isEmpty()) {
            return photo.getFilePath();
        } else {
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(photo.getFileId());
            try {
                File file = execute(getFileMethod);
                return file.getFilePath();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}