package com.p14osheagmail.okgoogleclone;

import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SpeechRecognizerManager.OnResultListener, TextToSpeech.OnInitListener{

    TextView s;
    private TextToSpeech tts;
    private SpeechRecognizerManager mSpeechRecognizerManager;
    private int MY_DATA_CHECK_CODE = 0;
    TextView voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setmSpeechRecognizerManager();

        voice = findViewById(R.id.voice);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }

    void setmSpeechRecognizerManager(){
        mSpeechRecognizerManager = new SpeechRecognizerManager(this);
        mSpeechRecognizerManager.setOnResultListner(this);
    }

    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                tts = new TextToSpeech(this, this);
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if(tts.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    private void recognition(String text){
        Log.e("Speech",""+text);
        String[] speech = text.split(" ");

        voice.setText(text);

        if(text.contains("what") && text.contains("time")){
            SimpleDateFormat digitalTime = new SimpleDateFormat("HH:mm");
            SimpleDateFormat analogTime = new SimpleDateFormat("HH:mm a");
            Date now = new Date();
            speak("The time is " + digitalTime.format(now) + " .or " + analogTime.format(now));
        }

        if(text.contains("what") && text.contains("date")){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c.getTime());
            speak(formattedDate);
        }

        if(text.contains("thank you")){
            speak("Thank you too ");
        }
    }

    @Override
    public void OnResult(ArrayList<String> commands) {
        //mSpeechRecognizerManager.destroy();
        StringBuilder text = new StringBuilder("");
        for(String command:commands) {
            text.append(command).append(" ");
        }
        recognition(text.toString());
    }
}
