package com.delta.faultprocessing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mzule.activityrouter.annotation.Router;

@Router(value ="FaultActivity", stringParams = "o")
public class FaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fault);
    }
}
