package com.example.gemini.CustomClasses;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Collections;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

public class DistilGPT2Generator {
    private OrtEnvironment env;
    private OrtSession session;
    private Context context;

    private DistilGPT2Tokenizer tokenizer;


    public DistilGPT2Generator(Context context) {
        this.context = context;
        try {
            // Initialize ONNX Runtime environment
            env = OrtEnvironment.getEnvironment();
            this.tokenizer = new DistilGPT2Tokenizer(context);
            // Load the ONNX model from assets
            session = env.createSession(loadModelFile(context));
        } catch (Exception e) {
            Log.e("TAG", "DistilGPT2Generator: " + e.getMessage() );
        }
    }

    private String loadModelFile(Context context) throws Exception {
        AssetManager assetManager = context.getAssets();
        File modelFile = new File(context.getFilesDir(), "distilgpt2.onnx");
        try (InputStream inputStream = assetManager.open("distilgpt2.onnx");
             FileOutputStream outputStream = new FileOutputStream(modelFile)) {
            byte[] buffer = new byte[2048];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return modelFile.getPath();
        }
    }
    public String generateText(String prompt) {
        try {
            // Preprocess the input text to token IDs
            if (tokenizer!=null){
                float[] inputIds = tokenizer.preprocess(prompt);
                OnnxTensor inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputIds), new long[]{1, inputIds.length});
                OrtSession.Result result = session.run(Collections.singletonMap("input_ids", inputTensor));
                float[] outputIds = (float[]) result.get(0).getValue();
                return tokenizer.postProcess(outputIds);

            }

        } catch (Exception e) {
//            Log.e(TAG, "generateText:  " + e.getMessage() );
            e.printStackTrace();
            return null;
        }
        return "Tokenizer is null!";
    }


}
