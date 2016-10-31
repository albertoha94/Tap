package com.albertoha94.games.Tap;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;

/**
 * Created by Albertoha94 on 15/01/2016.
 */
public class F_Table extends ListFragment {

    /*--VARIABLES---------------------------------------------------------------------------------*/

    public static final String ARG_DATA = "ARG1";
    int   _dif;

    /*--METHODS-----------------------------------------------------------------------------------*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle b = getArguments();
        _dif = b.getInt(ARG_DATA);
        return inflater.inflate(R.layout.fragment_leaderboards, null, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Adapter_Leaderboards ra = new Adapter_Leaderboards(getContext(), _dif);
        setListAdapter(ra);

    }
}
