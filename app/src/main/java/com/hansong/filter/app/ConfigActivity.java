package com.hansong.filter.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.hansong.filter.R;
import com.hansong.filter.core.IBlocker;
import com.hansong.filter.core.IFilter;
import com.hansong.filter.impl.LocationFilter;
import com.hansong.filter.impl.SystemContactFilter;
import com.hansong.filter.impl.TimeRangFilter;

import java.util.Set;

import static com.hansong.filter.utils.Constants.*;

/**
 * Created by xhans on 2016/7/2 0002.
 */
public class ConfigActivity extends Activity {

    public static String TAG = ConfigActivity.class.getName();

    private Button btnBlackList;
    private Switch swStranger;
    private Switch swTime;
    private Switch swLocation;
    private AlertDialog alertSetTime;
    private AlertDialog alertSetLocation;
    private boolean[] checkItems;
    private View setTimeView;
    private EditText etStartTime;
    private EditText etEndTime;
    private SharedPreferences sharedPreferences;

    private TimeRangFilter timeRangFilter;
    private SystemContactFilter systemContactFilter;
    private LocationFilter locationFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configs);

        checkItems = new boolean[PROVINCES.length];

        sharedPreferences = this.getSharedPreferences(FILTER_PROS, Context.MODE_PRIVATE);
        setupFilter();
        initView();
        setupSwitchTime();
        setupSwitchStranger();
        setupSwitchLocation();

    }

    private void setupFilter() {
        FilterApp filterApp = (FilterApp) getApplication();
        IBlocker iBlocker = filterApp.getiBlocker();
        timeRangFilter = (TimeRangFilter) iBlocker.getFilter(TIME_RANGE_FILTER);
        systemContactFilter = (SystemContactFilter) iBlocker.getFilter(CONTACT_FILTER);
        locationFilter = (LocationFilter) iBlocker.getFilter(LOCATION_FILTER);
    }

    private void initView() {

        btnBlackList = (Button) findViewById(R.id.btn_black_list);
        swLocation = (Switch) findViewById(R.id.sw_location);
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
        swStranger.setChecked(sharedPreferences.getBoolean(USE_CONTACT, false));

        swStranger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(USE_CONTACT, isChecked);
                editor.apply();

                Log.d(TAG, "陌生人拦截配置开关：" + isChecked);
                if (isChecked) {
                    systemContactFilter.open();
                } else {
                    systemContactFilter.close();
                }
            }
        });
    }

    private void setupSwitchLocation() {
        initSwitchLocation();

        swLocation.setChecked(sharedPreferences.getBoolean(USE_LOCATION, false));
        swLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                applyChanges(isChecked, USE_LOCATION, locationFilter, alertSetLocation);
                Log.d(TAG, "时间拦截配置开关：" + isChecked);
            }
        });
    }

    private void applyChanges(boolean isChecked, String setting, IFilter filter, AlertDialog alertDialog) {
        if (isChecked) {
            alertDialog.show();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(setting, isChecked);
        editor.apply();

        if (isChecked) {
            filter.open();
        } else {
            filter.close();
        }
    }

    private void initSwitchLocation() {
        //初始化选中的省份
        for (String pro : locationFilter.getProvinces()) {
            for (int i = 0; i < PROVINCES.length; i++) {
                if (pro.equals(PROVINCES[i])) {
                    checkItems[i] = true;
                    break;
                }
            }
        }
        alertSetLocation = new AlertDialog.Builder(this)
                .setTitle("选择要拦截的归属地")
                .setMultiChoiceItems(PROVINCES, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkItems[which] = isChecked;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(PROVINCES[which], isChecked);
                        editor.apply();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Set<String> provinces = locationFilter.getProvinces();
                        for (int i = 0; i < checkItems.length; i++) {
                            if (checkItems[i]) {
                                provinces.add(PROVINCES[i]);
                                Log.d(TAG, "添加拦截归属地：" + PROVINCES[i]);
                            }
                        }
                    }
                })
                .create();
    }

    private void initSwitchTime() {

        //加载设置时间范围的view
        final LayoutInflater inflater = this.getLayoutInflater();
        setTimeView = inflater.inflate(R.layout.view_set_time, null, false);
        etStartTime = (EditText) setTimeView.findViewById(R.id.et_start_time);
        etEndTime = (EditText) setTimeView.findViewById(R.id.et_end_time);

        etStartTime.setText(sharedPreferences.getInt(START_TIME, 0) + "");
        etEndTime.setText(sharedPreferences.getInt(END_TIME, 0) + "");
        swTime.setChecked(sharedPreferences.getBoolean(USE_TIME_RANGE, false));

        setTimeView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startTime = Integer.parseInt(etStartTime.getText().toString());
                int endTime = Integer.parseInt(etEndTime.getText().toString());
                if (isValidHour(startTime) && isValidHour(endTime)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(START_TIME, startTime);
                    editor.putInt(END_TIME, endTime);
                    editor.apply();
                    timeRangFilter.setEndHour(endTime);
                    timeRangFilter.setStartHour(startTime);
                    if (alertSetTime.isShowing()) {
                        alertSetTime.dismiss();
                    }
                } else {
                    Toast.makeText(ConfigActivity.this, "输入的时间必须在0~24之间", Toast.LENGTH_LONG).show();
                }
            }
        });
        alertSetTime = new AlertDialog.Builder(this).setTitle("设置拦截时间范围")
                .setView(setTimeView).create();

    }

    private void setupSwitchTime() {

        initSwitchTime();

        swTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "时间拦截配置开关：" + isChecked);
                applyChanges(isChecked, USE_TIME_RANGE, timeRangFilter, alertSetTime);
            }
        });
    }

    private boolean isValidHour(int hour) {
        return hour >= 0 && hour <= 24;
    }
}
