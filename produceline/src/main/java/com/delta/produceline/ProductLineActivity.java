package com.delta.produceline;

import android.os.Bundle;

import com.delta.ameslibs.DumpExtrasActivity;
import com.github.mzule.activityrouter.annotation.Router;

@Router(value = "productLine", stringParams = "o")
public class ProductLineActivity extends DumpExtrasActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_produceline);
    }
}
