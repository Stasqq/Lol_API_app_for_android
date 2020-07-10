package com.example.lolapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class MatchActivity extends AppCompatActivity {

    private JSONObject json;

    private int duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        Bundle b = getIntent().getExtras();
        if(b != null){
            try {
                json = new JSONObject(b.getString("matchData"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
