package com.vixir.finalproject.perfectday.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.vixir.finalproject.perfectday.R;
import com.vixir.finalproject.perfectday.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CreateTaskBySpeech extends AppCompatActivity {

    private static final String TAG = CreateTaskBySpeech.class.getName();
    @BindView(R.id.txtSpeechInput)
    protected TextView txtSpeechInput;

    protected final int REQ_CODE_SPEECH_INPUT = 127;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_input);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        } else if (!isVoiceInteraction()) {
            Log.e(TAG, "Not voice interaction");
            finish();
            return;
        }
    }

    private static boolean needPermissions(Activity createTaskBySpeech) {
//        if(createTaskBySpeech.permissio)
        return false;
    }


    @OnClick(R.id.btnSpeak)
    public void onSpeakButtonClick() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    if (null != this) {
                        int[] rainbow = this.getResources().getIntArray(R.array.picker_color);
                        Utils.addItem(this, result.get(0), rainbow[new Random().nextInt(rainbow.length)]);
//                        this.getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
//                        Utils.updateWidget(getActivity());
                        onBackPressed();
                    }
                }
                break;
            }

        }
    }
}
