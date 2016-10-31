package com.albertoha94.games.Tap;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Class: A_Achievements.java
 * Summary:         This class is used to show the achievements of the app. We use a custom
 *                  arrayAdapter to get the information and adjust its to the layout.
 * @author Albertoha94
 * @version 1
 * Changes------------------------------------------------------------------------------------------
 * 15/Mar/16:                   -Updated the whole class with its respective documentation.
 *                              -Added some code to make the rows unclickable.
 * 19/Mar/16:                   -Changed the code so it shows an image as an icon.
 */
public class A_Achievements extends AppCompatActivity {

    /*--VARIABLES---------------------------------------------------------------------------------*/

    private Activity _a;

    /*--METHODS-----------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState      );
        setContentView(R.layout.activity_stats);
        declare();

    }

    public void declare() {

        /*--VARIABLES-----------------------------------------------------------------------------*/

        _a         = A_Achievements.this;
        Context _c = A_Achievements.this;
        ListView _lv_main = (ListView) findViewById(R.id.lv_others);
        Typeface font     = Typeface.createFromAsset(getAssets(), "gamefont.otf");
        AchievementAdapter _aa = new AchievementAdapter(_c, getAchievements(), font);

        /*--ASSIGN ADAPTER------------------------------------------------------------------------*/

        _lv_main.setAdapter(_aa);

    }

    public Achieve[] getAchievements(){

        String[] names = getResources().getStringArray(R.array.achievement_name);
        String[] desc  = getResources().getStringArray(R.array.achievement_desc);
        String[] value = new String[11];
        int   [] max   = getResources().getIntArray(R.array.achievement_values_max );
        String[] state = getResources().getStringArray(R.array.GD_ACHIEVEMENT_STATE);
        Achieve[] d    = new Achieve[names.length];

        /*--FILL VALUES---------------------------------------------------------------------------*/

        value[0] = GM.GD_TOTAL_GAMES;
        System.arraycopy(GM.GD_COLORS, 0, value, 1, value.length - 1);

        /*----------------------------------------------------------------------------------------*/

        for(int i = 0; i < d.length; i++) {

            int val;
            if(i > 3)
                val = GM.readGData(_a, value[((i - 4) / 3) > 0 ? (i - 4) / 3 + 1:1]);
            else
                val = GM.readGData(_a,value[0]);

            d[i]    = new Achieve(names[i], desc[i], val, max[i], GM.readAchievementData(_a, state[i]));

        }

        return  d;

    }



    public static class AchievementAdapter extends ArrayAdapter<Achieve> {

        /*--VARIABLES-----------------------------------------------------------------------------*/

        private Achieve[] _a;
        private Context   _c;
        private Typeface  _f;

        /*--METHODS-------------------------------------------------------------------------------*/

        public AchievementAdapter(Context context, Achieve[] objects, Typeface oFont) {

            super(context, 0, objects);
            _a = objects;
            _c = context;
            _f = oFont;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Achieve d = _a[position];
            LayoutInflater inflater = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_achievements, parent, false);
            rowView.setEnabled(false);

            /*--ASSIGN VALUES---------------------------------------------------------------------*/

            TextView name  = (TextView)rowView.findViewById(R.id.tv_achievename);
            TextView desc  = (TextView)rowView.findViewById(R.id.tv_achievedesc);
            ProgressBar pb = (ProgressBar)rowView.findViewById(R.id.pb_achieve );
            ImageView icon  = (ImageView)rowView.findViewById(R.id.iv_achievestat);
            if(d.is_completed())
                icon.setImageResource(GM.ICONS[position]);
            else
                icon.setImageResource(GM.ICON_DEFAULT);

            name.setText(d.get_text());
            desc.setText(d.get_desc());
            name.setTypeface(_f);
            desc.setTypeface(_f);
            pb.setMax(d.get_max());
            pb.setProgress(d.get_value());
            return rowView;

        }

    }

    private class Achieve {

        /*--VARIABLES-----------------------------------------------------------------------------*/

        private String  _desc ,_text;
        private int     _value, _max;
        private boolean   _completed;

        /*--METHODS-------------------------------------------------------------------------------*/

        public Achieve(String _text, String ODesc, int _value, int _max, boolean _completed) {

            this._max = _max;
            this._text = _text;
            _desc = ODesc;
            this._value = _value;
            this._completed = _completed;

        }

        public String get_text() {

            return _text;

        }

        public int get_value() {

            return _value;

        }

        public int get_max() {

            return _max;

        }

        public boolean is_completed() {

            return _completed;

        }

        public String get_desc() {

            return _desc;

        }

    }

}
