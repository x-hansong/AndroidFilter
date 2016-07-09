package com.hansong.filter.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.hansong.filter.R;
import com.hansong.filter.core.IBlocker;
import com.hansong.filter.impl.NumeralFilter;
import com.hansong.filter.impl.TimeRangFilter;

import static com.hansong.filter.utils.Constants.*;

/**
 * Created by xhans on 2016/7/2 0002.
 */
public class ConfigActivity extends Activity {

    public static String TAG = ConfigActivity.class.getName();

    private Button btnBlackList;
    private Switch swStranger;
    private Switch swTime;
    private Button btnLocation;
    private AlertDialog alert;
    private AlertDialog.Builder builder;
    private View alertView;
    private EditText etStartTime;
    private EditText etEndTime;
    private SharedPreferences sharedPreferences;
    private TimeRangFilter timeRangFilter;
    private NumeralFilter whiteFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configs);

        sharedPreferences = this.getSharedPreferences(FILTER_PROS, Context.MODE_PRIVATE);
        setupFilter();
        initView();
        setupSwitchTime();
        setupSwitchStranger();

    }

    private void setupFilter() {
        FilterApp filterApp = (FilterApp) getApplication();
        IBlocker iBlocker = filterApp.getiBlocker();
        timeRangFilter = (TimeRangFilter) iBlocker.getFilter(TIME_RANGE_FILTER);
        whiteFilter = (NumeralFilter) iBlocker.getFilter(WHITE_LIST_FILTER);
    }

    private void initView() {

        btnBlackList = (Button) findViewById(R.id.btn_black_list);
        btnLocation = (Button) findViewById(R.id.btn_location);
        swStranger = (Switch) findViewById(R.id.sw_use_stranger);
        swTime = (Switch) findViewById(R.id.sw_use_time);


        btnBlackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfigActivity.this, BlackListActivity.class));
            }
        });
    }

    private void setupSwitchStranger() {
        swStranger.setChecked(sharedPreferences.getBoolean(USE_WHITE_LIST, false));

        swStranger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(USE_WHITE_LIST, isChecked);
                editor.apply();

                Log.d(TAG, "陌生人拦截配置开关：" + isChecked);
                if (isChecked) {
                    whiteFilter.open();
                } else {
                    whiteFilter.close();
                }
            }
        });
    }

    private void initSwitchTime() {

        //加载设置时间范围的view
        final LayoutInflater inflater = this.getLayoutInflater();
        alertView = inflater.inflate(R.layout.view_set_time, null, false);
        etStartTime = (EditText) alertView.findViewById(R.id.et_start_time);
        etEndTime = (EditText) alertView.findViewById(R.id.et_end_time);

        etStartTime.setText(sharedPreferences.getInt(START_TIME, 0) + "");
        etEndTime.setText(sharedPreferences.getInt(END_TIME, 0) + "");
        swTime.setChecked(sharedPreferences.getBoolean(USE_TIME_RANGE, false));

        alertView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startTime = Integer.parseInt(etStartTime.getText().toString());
                int endTime = Integer.parseInt(etEndTime.getText().toString());
                if (isValidHour(startTime) && isValidHour(endTime)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(START_TIME, startTime);
                    editor.putInt(END_TIME, endTime);
                    editor.apply();
                    timeRangFilter.setmEndHour(endTime);
                    timeRangFilter.setmStartHour(startTime);
                    if (alert.isShowing()) {
                        alert.dismiss();
                    }
                } else {
                    Toast.makeText(ConfigActivity.this, "输入的时间必须在0~24之间", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder = new AlertDialog.Builder(this);
        alert = builder.setTitle("设置拦截时间范围")
                .setView(alertView).create();

    }

    private void setupSwitchTime() {

        initSwitchTime();

        swTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alert.show();
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(USE_TIME_RANGE, isChecked);
                editor.apply();

                Log.d(TAG, "时间拦截配置开关：" + isChecked);

                if (isChecked) {
                    timeRangFilter.open();
                } else {
                    timeRangFilter.close();
                }
            }
        });
    }

    private boolean isValidHour(int hour) {
        return hour >= 0 && hour <= 24;
    }
}
