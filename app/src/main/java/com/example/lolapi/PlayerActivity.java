package com.example.lolapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class PlayerActivity extends AppCompatActivity {

    private Player playerInfo;
    private String playerName;
    private JSONObject playerJSON = null;
    private JSONObject matchHistory = null;
    private ArrayList<Match> matchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Bundle b = getIntent().getExtras();
        if(b != null){
            playerName = b.getString("name");
        }

        new DownloadTask().execute("https://eun1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + reName(playerName) + "?api_key=RGAPI-c55e0643-cc47-4807-bfb4-bdd0c672e24a");
    }


    private String reName(String name){
        String output="";

        for(int i=0;i<name.length();i++){
            if(name.charAt(i) != ' '){
                output += name.charAt(i);
            }else{
                output += "%20";
            }
        }

        return output;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if(playerJSON == null && matchHistory == null){
                    playerJSON = new JSONObject(result);
                    playerInfo = new Player(new JSONObject(result));
                    new DownloadTask().execute("https://eun1.api.riotgames.com/lol/match/v4/matchlists/by-account/" + playerJSON.getString("accountId") + "?api_key=RGAPI-c55e0643-cc47-4807-bfb4-bdd0c672e24a");
                }else if(playerJSON != null && matchHistory == null){
                    matchHistory = new JSONObject(result);
                    matchList = new ArrayList<Match>();
                    JSONArray jArray = matchHistory.getJSONArray("matches");
                    for(int i = 0; i < 20 ;i++){
                        new DownloadTask().execute("https://eun1.api.riotgames.com/lol/match/v4/matches/" + jArray.getJSONObject(i).getString("gameId") + "?api_key=RGAPI-c55e0643-cc47-4807-bfb4-bdd0c672e24a");
                    }
                }else if(playerJSON != null && matchHistory != null){
                    matchList.add(new Match(new JSONObject(result),playerName));
                    if(matchList.size() == 20){
                        end();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = convertInputStreamToString(is, StandardCharsets.UTF_8);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String convertInputStreamToString(InputStream stream, Charset charset) throws IOException {
        try (Scanner scanner = new Scanner(stream, charset.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    private void showPlayer(){
        TextView playerName = (TextView) findViewById(R.id.playerName);
        playerName.setText(playerInfo.getName());

        TextView playerLvl = (TextView) findViewById(R.id.playerLvl);
        playerLvl.setText(String.valueOf(playerInfo.getSummonerLevel()));

        TextView playerRank = (TextView) findViewById(R.id.playerRank);
        String temp = playerInfo.getSoloDuo().getTier() + " " + playerInfo.getSoloDuo().getRank();
        playerRank.setText(temp);

        TextView firstCh = (TextView) findViewById(R.id.firstChamp);
        firstCh.setText(playerInfo.getFirst().getChampionInfoAsString());

        TextView secondCh = (TextView) findViewById(R.id.secondChamp);
        secondCh.setText(playerInfo.getSecond().getChampionInfoAsString());

        TextView thirdCh = (TextView) findViewById(R.id.thirdChamp);
        thirdCh.setText(playerInfo.getThird().getChampionInfoAsString());

    }

    private void end(){
        showPlayer();

        MatchAdapter adapter = new MatchAdapter(PlayerActivity.this,matchList);
        ListView listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlayerActivity.this, MatchActivity.class);
                Bundle b = new Bundle();
                b.putString("matchData",matchList.get(position).getJson().toString());
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

}
