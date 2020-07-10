package com.example.lolapi;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Player {

    private int key = 0;

    private String id;
    private String accountId;
    private String puuid;
    private String name;
    private int profileIconId;
    private long revisionDate;
    private int summonerLevel;

    private Rank tft, soloDuo, flex;
    private ChampionInfo first, second, third;


    public Player(JSONObject playerJSON){
        try {
            id = playerJSON.getString("id");
            accountId = playerJSON.getString("accountId");
            puuid = playerJSON.getString("puuid");
            name = playerJSON.getString("name");
            profileIconId = playerJSON.getInt("profileIconId");
            revisionDate = playerJSON.getLong("revisionDate");
            summonerLevel = playerJSON.getInt("summonerLevel");

            tft = new Rank();
            tft.setTier("a");

            new DownloadTask().execute("https://eun1.api.riotgames.com/lol/league/v4/entries/by-summoner/" + id + "?api_key=RGAPI-c55e0643-cc47-4807-bfb4-bdd0c672e24a");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                if(key == 0){
                    key = 1;
                    tft = new Rank();
                    soloDuo = new Rank();
                    flex = new Rank();
                    JSONArray jResult = new JSONArray(result);

                    JSONObject temp;

                    for(int i = 0; i<jResult.length();i++){
                        temp = jResult.getJSONObject(i);

                        if(temp.getString("queueType").equals("RANKED_SOLO_5x5")){
                            soloDuo.setRank(temp.getString("rank"));
                            soloDuo.setTier(temp.getString("tier"));
                        }else if(temp.getString("queueType").equals("RANKED_TFT")){
                            tft.setRank(temp.getString("rank"));
                            tft.setTier(temp.getString("tier"));
                        }else if(temp.getString("queueType").equals("RANKED_FLEX_SR")){
                            flex.setRank(temp.getString("rank"));
                            flex.setTier(temp.getString("tier"));
                        }
                    }



                    new DownloadTask().execute("https://eun1.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-summoner/" + id + "?api_key=RGAPI-c55e0643-cc47-4807-bfb4-bdd0c672e24a");
                }else{
                    JSONArray jResult = new JSONArray(result);

                    first = new ChampionInfo(jResult.getJSONObject(0).getInt("championId"),jResult.getJSONObject(0).getInt("championPoints"),jResult.getJSONObject(0).getInt("championLevel"));
                    second = new ChampionInfo(jResult.getJSONObject(1).getInt("championId"),jResult.getJSONObject(1).getInt("championPoints"),jResult.getJSONObject(1).getInt("championLevel"));
                    third = new ChampionInfo(jResult.getJSONObject(2).getInt("championId"),jResult.getJSONObject(2).getInt("championPoints"),jResult.getJSONObject(2).getInt("championLevel"));
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


    public String getName() {
        return name;
    }

    public Rank getSoloDuo() {
        return soloDuo;
    }

    public ChampionInfo getFirst() {
        return first;
    }

    public ChampionInfo getSecond() {
        return second;
    }

    public ChampionInfo getThird() {
        return third;
    }

    public int getSummonerLevel() {
        return summonerLevel;
    }
}
