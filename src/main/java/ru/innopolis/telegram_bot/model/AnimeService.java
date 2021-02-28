package ru.innopolis.telegram_bot.model;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnimeService {

    private final ZooModel<Image, Image> animeModel;

    public AnimeService(
            ZooModel<Image, Image> animeModel) {
        this.animeModel = animeModel;
    }

    public Image transform(Image image) {
        try (Predictor<Image, Image> predictor = animeModel.newPredictor()) {
            return predictor.predict(image);
        } catch (TranslateException e) {
            log.error(e.getMessage());
        }
        return null;
    }

}

