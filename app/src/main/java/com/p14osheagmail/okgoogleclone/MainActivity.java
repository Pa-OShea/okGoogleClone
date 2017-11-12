package com.p14osheagmail.okgoogleclone;

import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s = (TextView) findViewById(R.id.hello);
        setmSpeechRecognizerManager();

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

        if(text.contains("weather")){
            speak("it is probably raining");
        }

        if(text.contains("play") && text.contains("game")){
            //for the memory game
        }

        if(text.contains("play") && text.contains("music")){
            //for music
        }

        if(text.contains("lights")) {
            if (text.contains("turn on")) {
                speak("lights turned on");
            } else if (text.contains("turn off")) {
                speak("lights turned off");
            }
        }

        if(text.contains("wake me up at") || text.contains("alarm")){
            speak(speech[speech.length-1]);
            String[] time = speech[speech.length-1].split(":");
            String hour = time[0];
            String minutes = time[1];
            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
            i.putExtra(AlarmClock.EXTRA_HOUR, Integer.valueOf(hour));
            i.putExtra(AlarmClock.EXTRA_MINUTES, Integer.valueOf(minutes));
            startActivity(i);
            speak("Setting alarm to ring at " + hour + ":" + minutes);
        }

        if(text.contains("thank you")){
            speak("Thank you too ");
        }
    }

    @Override
    public void OnResult(ArrayList<String> commands) {
        StringBuilder text = new StringBuilder("");
        for(String command:commands) {
            text.append(command).append(" ");
        }
        recognition(text.toString());
    }
}
