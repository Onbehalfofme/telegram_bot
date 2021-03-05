package ru.innopolis.telegram_bot.model;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.BufferedImageFactory;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.Image.Flag;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.pytorch.engine.PtNDArray;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnimeService {

    private final Model animeModel;

    public AnimeService(Model animeModel) {
        this.animeModel = animeModel;
    }

    public Image transform(Image image) throws IOException, MalformedModelException {

        try (Predictor<Image, Image> predictor = animeModel.newPredictor(new AnimeTranslator())) {
            return predictor.predict(image);
        } catch (TranslateException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static final class AnimeTranslator implements Translator<Image, Image> {

        @Override
        public Image processOutput(TranslatorContext translatorContext, NDList ndList) {
            BufferedImageFactory factory = new BufferedImageFactory();
            PtNDArray arr = (PtNDArray) ndList.get(0).add(1).mul(255).toType(DataType.INT8, false);
            arr = arr.transpose(1, 2, 0);
            return factory.fromNDArray(arr);
        }

        @Override
        public NDList processInput(TranslatorContext ctx, Image input) {
            PtNDArray array = (PtNDArray) input.toNDArray(ctx.getNDManager(), Flag.COLOR);
            array = (PtNDArray) NDImageUtils.resize(array, 256, 256);
            return new NDList(NDImageUtils.toTensor(array));
        }

        @Override
        public Batchifier getBatchifier() {
            return Batchifier.STACK;
        }
    }

}

