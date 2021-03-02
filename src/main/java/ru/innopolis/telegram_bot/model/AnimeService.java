package ru.innopolis.telegram_bot.model;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.translate.TranslateException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.innopolis.telegram_bot.config.DjlConfig.AnimeTranslator;

@Slf4j
@Service
public class AnimeService {


    public Image transform(Image image) throws IOException, MalformedModelException {
        Path modelDir = Paths.get("/Users/onbehalfofme/IdeaProjects/telegram_bot/src/main/resources/traced_resnet_model.pt");
        Model animeModel = Model.newInstance("anime model");
        animeModel.load(modelDir);

        try (Predictor<Image, Image> predictor = animeModel.newPredictor(new AnimeTranslator())) {
            return predictor.predict(image);
        } catch (TranslateException e) {
            log.error(e.getMessage());
        }
        return null;
    }

}

