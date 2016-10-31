package com.albertoha94.games.Tap;


import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Class: A_Statistics.java
 * Summary:         This class is used to show the stats of the app. We use a custom arrayAdapter
 *                  to get the information and adjust its to the layout.
 * @author Albertoha94
 * @version 1
 * Changes------------------------------------------------------------------------------------------
 * 15/Mar/16:                   -Updated the whole class with its respective documentation.
 *                              -Added some code to make the rows unclickable.
 */
public class A_Statistics extends AppCompatActivity {

    /*--VARIABLES---------------------------------------------------------------------------------*/
    private Activity _a;

    /*--METHODS-----------------------------------------------------------------------------------*/
    /*
     *Summary:          Method used when the activity is created. It draws the activity_stats layout
     *                  and initializes the rest of the class.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState      );
        setContentView(R.layout.activity_stats);
        declare();

    }

    /*
     *Summary:          Main method of the class, it loads the layouts listview and sets an adapter
     *                  to it.
     */
    public void declare() {

        /*--VARIABLES-----------------------------------------------------------------------------*/
        Context _c = A_Statistics.this;
        _a         = A_Statistics.this;
        ListView _lv_main = (ListView) findViewById(R.id.lv_others);
        Typeface font     = Typeface.createFromAsset(getAssets(), "gamefont.otf");
        StatsAdapter _sa  = new StatsAdapter(_c, getStatList(), font);

        /*Configuring the listview----------------------------------------------------------------*/
        _lv_main.setAdapter(_sa);

    }

    /*
     *Summary:          Its used when inflating the adapter. This method creates an Arraylist
     *                  containing all the row's the list will will have.
     *Returns:          An arraylist containing all the statistics within the game.
     */
    public ArrayList getStatList(){

        ArrayList d = new ArrayList();
        String[] ar = getResources().getStringArray(R.array.stat_names);

        /*--HEADER--------------------------------------------------------------------------------*/
        d.add(new Header(ar[0]));

        /*----------------------------------------------------------------------------------------*/
        d.add(new Stat(ar[1], GM.readGData(_a, GM.GD_TOTAL_GAMES)));
        d.add(new Stat(ar[2], GM.readGData(_a, GM.GD_PERFECT_G  )));
        d.add(new Stat(ar[3], GM.readGData(_a, GM.GD_LONGSTREAK )));

        /*--HEADER--------------------------------------------------------------------------------*/
        d.add(new Header(ar[4]));

        /*----------------------------------------------------------------------------------------*/
        d.add(new Stat(ar[5], GM.readGData(_a, GM.GD_MING    )));
        d.add(new Stat(ar[6], GM.readGData(_a, GM.GD_TAPGAMES)));
        d.add(new Stat(ar[7], GM.readGData(_a, GM.GD_CMING   )));
        d.add(new Stat(ar[8], GM.readGData(_a, GM.GD_CTAPG   )));

        /*--HEADER--------------------------------------------------------------------------------*/
        d.add(new Header(ar[9]));

        /*----------------------------------------------------------------------------------------*/
        d.add(new Stat(ar[10], GM.readGData(_a, GM.GD_TOTALBLOCK)));

        /*--COLORS--------------------------------------------------------------------------------*/
        d.add(new Stat(ar[11], GM.readGData(_a, GM.GD_COLORS[0]), R.drawable.c_awesome, true       ));
        d.add(new Stat(ar[12], GM.readGData(_a, GM.GD_COLORS[1]), R.drawable.c_air_force_blue, true));
        d.add(new Stat(ar[13], GM.readGData(_a, GM.GD_COLORS[2]), R.drawable.c_amber, true         ));
        d.add(new Stat(ar[14], GM.readGData(_a, GM.GD_COLORS[3]), R.drawable.c_amethyst, true      ));
        d.add(new Stat(ar[15], GM.readGData(_a, GM.GD_COLORS[4]), R.drawable.c_ao, true            ));
        d.add(new Stat(ar[16], GM.readGData(_a, GM.GD_COLORS[5]), R.drawable.c_turquoise, true     ));
        d.add(new Stat(ar[17], GM.readGData(_a, GM.GD_COLORS[6]), R.drawable.c_bronze, true        ));
        d.add(new Stat(ar[18], GM.readGData(_a, GM.GD_COLORS[7]), R.drawable.c_cadet_grey, true    ));
        d.add(new Stat(ar[19], GM.readGData(_a, GM.GD_COLORS[8]), R.drawable.c_cadnium_green, true ));
        d.add(new Stat(ar[20], GM.readGData(_a, GM.GD_COLORS[9]), R.drawable.c_awesome, true       ));
        return  d;

    }

    public static class StatsAdapter extends ArrayAdapter {

        /*--VARIABLES-----------------------------------------------------------------------------*/
        private Context    _c;
        private ArrayList  _s;
        private Typeface   _f;

        /*--METHODS-------------------------------------------------------------------------------*/
        /*
         * Summary:      Main constructor of the class.
         * Parameters:
         *  context:    Contains the context of the current activity.
         *  objects:    list of all the items to inflate.
         *  oFont:      Contains the font used inside the app.
         */
        public StatsAdapter(Context context, ArrayList objects, Typeface oFont) {

            super(context, 0, objects);
            _c = context;
            _s = objects;
            _f = oFont;

        }

        /*
         * Summary:      A method that draws the view that is used according to the position.
         * Parameters:
         *  position:   the current position of the selected view.
         *  convertView:the actual view of the current item.
         *  parent:     all the views within the adapter.
         * Returns:     The view is currently selected.
         */
        @Override
        public View getView( int position, View convertView, ViewGroup parent ) {

            Stat   d ;
            Header d2;
            LayoutInflater inflater = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView text, val;
            View color, rowView;
            if(position == 0 || position == 4 || position == 9) {

                d2 = (Header) _s.get(position);
                rowView = inflater.inflate(R.layout.row_stats_header, parent, false);
                rowView.setEnabled(false);
                text = (TextView)rowView.findViewById(R.id.tv_header      );
                text.setTypeface(_f);
                text.setText(d2.get_header());

            } else {

                d = (Stat) _s.get(position);
                rowView = inflater.inflate(R.layout.row_stats_normal, parent, false);
                rowView.setEnabled(false);
                text = (TextView)rowView.findViewById(R.id.tv_stattxt);
                val  = (TextView)rowView.findViewById(R.id.tv_statval);
                text.setTypeface(_f);
                val.setTypeface(_f );
                color   = rowView.findViewById(R.id.v_color);
                text.setText(d.get_data());
                val.setText(String.valueOf(d.get_value()));
                if(d._has_icon)
                    color.setBackground(ContextCompat.getDrawable(_c, d.get_i_src()));
                else
                    color.setVisibility(View.GONE);

            }

            return rowView;

        }

    }

    /*
     * Summary:         The following 2 classes are used to hold the information of the statistics
     *                  and the header used in the listview. The header just holds 1 string that is
     *                  the title and the Stat the information of the statistic. THe statistic can
      *                 be used in 2 ways: A string and its value or a string, value and icon.
     */
    private class Stat {

        /*--VARIABLES-----------------------------------------------------------------------------*/
        private String  _data         ;
        private int     _value, _i_src;
        private boolean _has_icon     ;

        /*--METHODS-------------------------------------------------------------------------------*/
        /*
         * Summary:  Basic constructor of the class. It is used when the row doesnt require an image.
         * Parameters:
         *  _data:   Holds the string ot be displayed in the row.
         *  _value:  The value of data in the same position as the row.
         */
        public Stat(String _data, int _value) {

            this._data  = _data ;
            this._value = _value;
            _i_src      = -1    ;
            _has_icon   = false ;

        }

        /*
         * Summary:  Basic constructor of the class. It is used when the row requires an image.
         * Parameters:
         *  _data:   Holds the string ot be displayed in the row.
         *  _value:  The value of data in the same position as the row.
         *  _i_src:  Specifies the image resource.
         *  _has_icon: Checks if the row has an icon.
         */
        public Stat(String _data, int _value, int _i_src, boolean _has_icon) {

            this._data = _data;
            this._value = _value;
            this._i_src = _i_src;
            this._has_icon = _has_icon;

        }

        /*--Getters-------------------------------------------------------------------------------*/
        public String get_data() {

            return _data;

        }

        public int get_value() {

            return _value;

        }

        public int get_i_src() {

            return _i_src;

        }

    }

    private class Header {

        /*--VARIABLES-----------------------------------------------------------------------------*/
        String _header;

        /*--Constructor---------------------------------------------------------------------------*/
        public Header(String _header) {

            this._header = _header;

        }

        /*--Getter--------------------------------------------------------------------------------*/
        public String get_header() {

            return _header;

        }

    }

}
