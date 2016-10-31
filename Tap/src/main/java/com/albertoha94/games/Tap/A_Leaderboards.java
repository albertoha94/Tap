package com.albertoha94.games.Tap;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Class: A_Leaderboards.java
 * Summary:         This class is used to show the leaderboards of the app. We use a custom
 *                  arrayAdapter to get the information and adjust its to the layout.
 * @author Albertoha94
 * @version 1
 * Changes------------------------------------------------------------------------------------------
 * 16/Mar/16:       -Added font styling and text size to the title strip.
 */
public class A_Leaderboards extends AppCompatActivity {

    /*--VARIABLES---------------------------------------------------------------------------------*/

    public static final String[] DATAFILES = {"minute.txt", "tap.txt", "cminute.txt", "ctap.txt"};

    /*--METHODS-----------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);
        declare();

    }

    public void declare(){

        ViewPager _vp_main = (ViewPager) findViewById(R.id.vp_leaderboards);
        Leaders _adapter = new Leaders(getSupportFragmentManager());
        _vp_main.setAdapter(_adapter);

        /*Styiling the pager title----------------------------------------------------------------*/
        Typeface font = Typeface.createFromAsset(getAssets(), "gamefont.otf"); //We get the font.
        PagerTitleStrip pts = (PagerTitleStrip)findViewById(R.id.pts_leaders);
        for (int i = 0; i < pts.getChildCount(); ++i) {             //We create childs for each title
            View nextChild = pts.getChildAt(i);                     //and then wee format the childs.
            if (nextChild instanceof TextView) {
                TextView textViewToConvert = (TextView)nextChild;
                textViewToConvert.setTypeface(font);
                textViewToConvert.setTextSize(getResources().getDimension(R.dimen.tv_leader_tstrip_textsize));
            }
        }

    }

    private class Leaders extends FragmentPagerAdapter {

        /*--VARIABLES-----------------------------------------------------------------------------*/

        public final int PAGES = 4;

        /*--METHODS-------------------------------------------------------------------------------*/

        public Leaders(FragmentManager fm) {

            super(fm);

        }

        @Override
        public F_Table getItem(int position) {

            Bundle d = new Bundle();
            d.putInt(F_Table.ARG_DATA, position);
            F_Table t = new F_Table();
            t.setArguments(d);
            return t;

        }

        @Override
        public CharSequence getPageTitle(int position) {

            return getResources().getStringArray(R.array.leaders_titles)[position];

        }

        @Override
        public int getCount() {

            return PAGES;

        }

    }

}
