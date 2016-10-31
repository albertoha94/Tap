package com.albertoha94.games.Tap;

/**
 * Created by Albertoha94 on 13/01/2016.
 */
public class Row {

    /*--VARIABLES-----------------------------------------------------------------------------*/

    public static final int EMPTY = -1;
    private int _score, _drawicon, _difficulty;
    private boolean _bind;

        /*--METHODS-------------------------------------------------------------------------------*/

    public Row(){

        _bind       = false;
        _score      = EMPTY;
        _drawicon   = EMPTY;
        _difficulty = EMPTY;

    }

    public Row(boolean _bind, int _score, int _drawicon, int _difficulty) {

        this._bind       = _bind      ;
        this._score      = _score     ;
        this._drawicon   = _drawicon  ;
        this._difficulty = _difficulty;

    }

    public Row(String OText){

        fromString(OText);

    }

    @Override
    public String toString() {

        return _bind + "," + _score + "," + _drawicon + "," + _difficulty + "@";

    }

    public void fromString(String OS) {

        String[] d  = OS.split(",");
        _bind       = Boolean.valueOf(d[0]);
        _score      = Integer.valueOf(d[1]);
        _drawicon   = Integer.valueOf(d[2]);
        _difficulty = Integer.valueOf(d[3]);

    }

    public int get_score() {

        return _score;

    }

    public void set_score(int _score) {

        this._score = _score;

    }

    public boolean is_bind() {

        return _bind;

    }

    public int get_drawicon() {

        return _drawicon;

    }

    public int get_difficulty() {

        return _difficulty;

    }

}
