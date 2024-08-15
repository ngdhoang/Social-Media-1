package com.GHTK.Social_Network.application.port.output;

import ai.djl.translate.TranslateException;
import ai.onnxruntime.OrtException;

public interface PhoBERTPortInput {
    boolean isToxic(String text) throws OrtException, TranslateException;
}
