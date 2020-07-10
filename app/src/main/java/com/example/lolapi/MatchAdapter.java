package com.example.lolapi;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MatchAdapter extends ArrayAdapter<Match> {

    public MatchAdapter(Activity context, ArrayList<Match> matches){
        super(context,0,matches);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.match_list_item,parent,false);
        }

        final Match currentMatch = getItem(position);

        ImageView image = (ImageView) listItemView.findViewById(R.id.championIcon);
        image.setImageResource(currentMatch.getChampion().getId());

        TextView winTextView = (TextView) listItemView.findViewById(R.id.win_text_view);
        if(currentMatch.isWin()){
            winTextView.setText("Victory");
            listItemView.setBackgroundColor(listItemView.getResources().getColor(R.color.matchWinColor));
        }else{
            winTextView.setText("Defeat");
            listItemView.setBackgroundColor(listItemView.getResources().getColor(R.color.matchLoseColor));
        }

        TextView kdaTextView = (TextView) listItemView.findViewById(R.id.kda_text_view);
        kdaTextView.setText(currentMatch.getKDAString());


        return listItemView;
    }
}
