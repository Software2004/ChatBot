package com.example.gemini.CustomClasses;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistilGPT2Tokenizer {
    private Map<String, Integer> vocab; // Mapping from tokens to IDs
    private String[] reverseVocab; // Mapping from IDs to tokens

    public DistilGPT2Tokenizer(Context context) {
        vocab = new HashMap<>();
        loadVocabulary(context);
    }

    private void loadVocabulary(Context context) {
        try {
            InputStream is = context.getAssets().open("vocab.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            int index = 0;
            List<String> vocabList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                vocab.put(line.trim(), index);
                vocabList.add(line.trim());
                index++;
            }
            reverseVocab = vocabList.toArray(new String[0]);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float[] preprocess(String text) {
        // Tokenize input text into tokens using the vocabulary
        String[] words = text.split(" ");
        List<Float> inputTokens = new ArrayList<>();

        for (String word : words) {
            Integer token = vocab.get(word);
            if (token != null) {
                inputTokens.add((float) token);
            } else {
                inputTokens.add((float) vocab.get("[UNK]")); // Unknown token
            }
        }

        // Convert List<Float> to float[]
        float[] tokenArray = new float[inputTokens.size()];
        for (int i = 0; i < inputTokens.size(); i++) {
            tokenArray[i] = inputTokens.get(i);
        }
        return tokenArray;
    }

    public String postProcess(float[] outputIds) {
        StringBuilder result = new StringBuilder();
        for (float id : outputIds) {
            int intId = (int) id;
            if (intId >= 0 && intId < reverseVocab.length) {
                result.append(reverseVocab[intId]).append(" ");
            }
        }
        return result.toString().trim();
    }
}
