package ru.innopolis.telegram_bot.config;

import ai.djl.Model;
import ai.djl.modality.cv.BufferedImageFactory;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.Image.Flag;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DjlConfig {

    @Value("${model.path}")
    private String modelPath;

    @Bean
    public Model animeModel() throws Exception {
        Model animeModel = Model.newInstance("anime model");
        animeModel.load(Paths.get(modelPath));
        return animeModel;
    }
}
