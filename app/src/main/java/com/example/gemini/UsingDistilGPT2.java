package com.example.gemini;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gemini.CustomClasses.DistilGPT2Generator;

public class UsingDistilGPT2 extends AppCompatActivity {
    private DistilGPT2Generator generator;
    private TextView outputTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using_distil_gpt2);

        outputTextView = findViewById(R.id.outputTextView);
        generator = new DistilGPT2Generator(this);

        String prompt = "Once upon a time";
        String generatedText = generator.generateText(prompt);
        outputTextView.setText(generatedText);
    }
}