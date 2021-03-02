package ru.innopolis.telegram_bot.config;

import ai.djl.modality.cv.BufferedImageFactory;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.Image.Flag;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DjlConfig {

//    @Bean
//    public ZooModel<Image, Image> animeModel() throws Exception {
//        Criteria<Image, Image> criteria =
//                Criteria.builder()
//                        .setTypes(Image.class, Image.class)
//                        .optModelUrls("")
//                        .optTranslator(new AnimeTranslator())
//                        .build();
//        return ModelZoo.loadModel(criteria);
//    }

    public static final class AnimeTranslator implements Translator<Image, Image> {

        @Override
        public Image processOutput(TranslatorContext translatorContext, NDList ndList) {
            BufferedImageFactory factory = new BufferedImageFactory();
            NDArray arr = ndList.get(0).toType(DataType.INT8, false);
            return factory.fromNDArray(arr);
        }

        @Override
        public NDList processInput(TranslatorContext ctx, Image input) {
            NDArray array = input.toNDArray(ctx.getNDManager(), Flag.COLOR);
            return new NDList(NDImageUtils.toTensor(array));
        }

        @Override
        public Batchifier getBatchifier() {
            return Batchifier.STACK;
        }
    }
}
