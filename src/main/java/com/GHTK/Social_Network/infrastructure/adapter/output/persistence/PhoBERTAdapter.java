package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.GHTK.Social_Network.application.port.output.PhoBERTPortInput;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PhoBERTAdapter implements PhoBERTPortInput {

    private OrtEnvironment env;
    private OrtSession session;
    private TokenizerPortAdapter tokenizerAdapter;

    private static final String MODEL_PATH = "raw-files/onnx/classifier.onnx";
    private static final String TOKENIZER_PATH = "raw-files/tokenizer.json";

    @PostConstruct
    public void init() throws OrtException {
        try {
            env = OrtEnvironment.getEnvironment();
            session = env.createSession(MODEL_PATH, new OrtSession.SessionOptions());
            tokenizerAdapter = new TokenizerPortAdapter(TOKENIZER_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize PhoBERT adapter", e);
        }
    }

    @PreDestroy
    public void cleanup() throws OrtException {
        if (session != null) session.close();
        if (env != null) env.close();
    }

    public boolean isToxic(String text) throws OrtException {
        tokenizerAdapter.encode(text);
        long[] inputIds = tokenizerAdapter.getIds();
        long[] inputAttentionMask = tokenizerAdapter.getAttentionMask();

        long[][] newInputIds = new long[1][inputIds.length];
        System.arraycopy(inputIds, 0, newInputIds[0], 0, inputIds.length);

        float[][] newAttentionMaskFloat = new float[1][inputAttentionMask.length];
        for (int i = 0; i < inputAttentionMask.length; i++) {
            newAttentionMaskFloat[0][i] = inputAttentionMask[i];
        }

        try (OnnxTensor idsTensor = OnnxTensor.createTensor(env, newInputIds);
             OnnxTensor maskTensor = OnnxTensor.createTensor(env, newAttentionMaskFloat)) {

            Map<String, OnnxTensor> modelInputs = Map.of("input_ids", idsTensor, "attention_mask", maskTensor);
            try (OrtSession.Result result = session.run(modelInputs)) {
                float[][] logits = (float[][]) result.get(0).getValue();
                System.out.println(logits.length);
                for (int i = 0; i < logits.length; i++) {
                    System.out.print("Mẫu " + (i + 1) + ": ");
                    for (int j = 0; j < logits[i].length; j++) {
                        System.out.printf("%.4f ", logits[i][j]);
                    }
                    System.out.println();
                }
                return findMaxIndex(logits[0]) == 1;
            }
        }
    }

    private int findMaxIndex(float[] arr) {
        int maxIndex = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}
