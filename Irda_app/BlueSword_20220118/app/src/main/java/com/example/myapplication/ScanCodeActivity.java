package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.components.SegmentView;


public class ScanCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        initView();
    }


    private void initView() {

        SegmentView segmentView = findViewById(R.id.segmentview);
        segmentView.contentView(new String[]{"线路1","线路2"},16);
        segmentView.setOnSegmentViewClickListener(new SegmentView.onSegmentViewClickListener() {
            @Override
            public void onSegmentViewClick(View view, int postion) {
                TextView textView=findViewById(view.getId());
                switch (postion){
                    case 0:

                        Toast.makeText(ScanCodeActivity.this, textView.getText(),Toast.LENGTH_SHORT).show();
                        break;
                    case 1:

                        Toast.makeText(ScanCodeActivity.this,textView.getText(),Toast.LENGTH_SHORT).show();
                        break;

                }

            }

        });

    }
}