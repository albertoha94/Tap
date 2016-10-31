package com.albertoha94.games.Tap;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Class: Adapter_Leaderboards.java
 * @author  Albertoha94
 * @version 1
 * Changes------------------------------------------------------------------------------------------
 * 15/Mar/16:               -Empty string for the leaderboards modified.
 *                          -Added a case that switches the text color according to the difficulty.
 *                          -Added some code to disable the clickable option.
 */
public class Adapter_Leaderboards extends ArrayAdapter {

    /*--VARIABLES---------------------------------------------------------------------------------*/

    private Context _c  ;
    private Row[]  _main;

    /*--METHODS-----------------------------------------------------------------------------------*/

        public Adapter_Leaderboards(Context context, int OGameMode) {

            super(context, 0,  GM.readLeaders(context, A_Leaderboards.DATAFILES[OGameMode]));
            _c        = context;
            _main     = GM.readLeaders(_c, A_Leaderboards.DATAFILES[OGameMode]);

        }

    @SuppressWarnings("deprecation")
    @Override
        public View getView(int position, View convertView, ViewGroup parent) {

        Row main = _main[position];
        int colorid = 0;
        switch (main.get_difficulty()){                 //Color according to the difficulty.
            case GM.DIF_EZ:
                colorid = R.color.ao;
                break;
            case GM.DIF_MED:
                colorid = R.color.amber;
                break;
            case GM.DIF_HARD:
                colorid = R.color.bronze;
                break;
            case GM.DIF_EX:
                colorid = R.color.auburn;
                break;

        }

        /*--LAYOUT FILES--------------------------------------------------------------------------*/
        View color, rootview;
        TextView place, score, dif;
        LayoutInflater inflater = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootview = inflater.inflate(R.layout.row_leaderboards, null);
        rootview.setEnabled(false);
        color = rootview.findViewById(R.id.v_color_leaderboards);
        place = (TextView)rootview.findViewById(R.id.tv_place_leaderboards);
        score = (TextView)rootview.findViewById(R.id.tv_score_leaderboards);
        dif   = (TextView)rootview.findViewById(R.id.tv_difficulty_leaderboards);

        /*--FONT----------------------------------------------------------------------------------*/
        Typeface font = Typeface.createFromAsset(_c.getAssets(), "gamefont.otf");
        place.setTypeface(font);
        score.setTypeface(font);
        dif.setTypeface(font);

        /*--ASSIGN VALUES-------------------------------------------------------------------------*/
        place.setText(String.valueOf(position + 1) + ". ");    //Position
        if(main.get_score() == Row.EMPTY)                     //Score
            score.setText(" - ");
        else
            score.setText(String.valueOf(main.get_score()));

        if(main.get_difficulty() == Row.EMPTY)                //Difficulty
            dif.setText(" - ");
        else {
            dif.setText(_c.getResources().getStringArray(R.array.difficulty_s)[main.get_difficulty()]);
            dif.setTextColor(_c.getResources().getColor(colorid));
        }

        if(main.is_bind())                                    //Color
            color.setBackground(ContextCompat.getDrawable(_c, main.get_drawicon()));
        else
            color.setVisibility(View.GONE);
        return rootview;

        }

}
