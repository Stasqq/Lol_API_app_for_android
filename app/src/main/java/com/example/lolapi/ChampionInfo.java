package com.example.lolapi;

public class ChampionInfo{
    public Champion champion;
    public int points;
    public int lvl;

    public ChampionInfo(int championId , int championPoints, int championLvl){
        champion = new Champion(championId);
        lvl=championLvl;
        points=championPoints;
    }

    public String getChampionInfoAsString(){
        String output = champion.getName() + ": " + String.valueOf(lvl) + " - " + String.valueOf(points);
        return output;
    }
}