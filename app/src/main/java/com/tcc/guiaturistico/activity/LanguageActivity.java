package com.tcc.guiaturistico.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.tcc.guiaturistico.R;

/**
 * Created by Andressa on 26/05/2018.
 */

public class LanguageActivity extends AppCompatActivity {
    public ExpandableListView elv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
    }
}
