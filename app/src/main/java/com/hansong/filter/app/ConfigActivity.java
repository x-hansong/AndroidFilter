package com.hansong.filter.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.hansong.filter.R;

/**
 * Created by xhans on 2016/7/2 0002.
 */
public class ConfigActivity extends Activity{

    private Button btnBlackList;
    private Button btnStranger;
    private Button btnTime;
    private Button btnLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configs);

        initView();
    }

    private void initView() {
        btnBlackList = (Button) findViewById(R.id.btn_black_list);
        btnLocation = (Button) findViewById(R.id.btn_location);
        btnTime = (Button) findViewById(R.id.btn_time_range);
        btnStranger = (Button) findViewById(R.id.btn_stranger);

        btnBlackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfigActivity.this, BlackListActivity.class));
            }
        });
    }
}
