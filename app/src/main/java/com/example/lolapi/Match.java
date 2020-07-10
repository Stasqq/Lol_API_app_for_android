package com.example.lolapi;

import org.json.JSONArray;
import org.json.JSONObject;


public class Match {

    private Champion champion;
    private boolean win;
    private int kill, deaths, assists;
    private JSONObject json;

    public Match(int championId, boolean win, int kill, int deaths, int assists) {
        champion = new Champion(championId);
        this.win = win;
        this.kill = kill;
        this.deaths = deaths;
        this.assists = assists;
    }

    public Match(JSONObject match, String name){
        try{
            JSONArray partList = match.getJSONArray("participantIdentities");
            json = match;
            JSONObject playerStats = null;
            for(int i=0;i<10;i++){
                if(partList.getJSONObject(i).getJSONObject("player").getString("summonerName").equals(name)){
                    playerStats = match.getJSONArray("participants").getJSONObject(i);
                }
            }

            champion = new Champion(playerStats.getInt("championId"));

            JSONObject stats = playerStats.getJSONObject("stats");
            this.kill = stats.getInt("kills");
            this.deaths = stats.getInt("deaths");
            this.assists = stats.getInt("assists");
            this.win = stats.getBoolean("win");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public JSONObject getJson() {
        return json;
    }

    public Champion getChampion() {
        return champion;
    }

    public boolean isWin() {
        return win;
    }

    public String getKDAString(){
        return Integer.toString(kill) + " / " + Integer.toString(deaths) + " / " + Integer.toString(assists);
    }

    public int getKill() {
        return kill;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }
}
