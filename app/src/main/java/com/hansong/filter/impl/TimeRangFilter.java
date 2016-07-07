package com.hansong.filter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.hansong.filter.core.AbsFilter;
import com.hansong.filter.core.IFilter;
import com.hansong.filter.core.MessageData;
import com.hansong.filter.utils.Constants;

import java.util.Calendar;

import static com.hansong.filter.utils.Constants.FILTER_PROS;
import static com.hansong.filter.utils.Constants.USE_TIME_RANGE;


/**
 * 按时段过滤
 * @author boyliang
 *
 */
public final class TimeRangFilter extends AbsFilter {


    public static String TAG = TimeRangFilter.class.getName();
    private int mStartHour;
    private int mEndHour;

    private TimeRangFilter(int starthour, int endhour){
        mStartHour = starthour;
        mEndHour = endhour;
    }

    public void setmStartHour(int mStartHour) {
        this.mStartHour = mStartHour;
    }

    public void setmEndHour(int mEndHour) {
        this.mEndHour = mEndHour;
    }

    @Override
    public int onFiltering(MessageData data) {

        Calendar now = Calendar.getInstance();
        int current_hour = now.get(Calendar.HOUR_OF_DAY);

        String phone = data.getString(MessageData.KEY_DATA);
        if(current_hour >= mStartHour && current_hour <= mEndHour){
            Log.d(TAG, "时间拦截：" + phone);
            return IFilter.OP_BLOCKED;
        }else{
            return IFilter.OP_SKIP;
        }
    }

    public static TimeRangFilter build(Context context) {
        //加载配置
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILTER_PROS, Context.MODE_PRIVATE);

        int startTime = sharedPreferences.getInt(Constants.START_TIME, 0);
        int endTime = sharedPreferences.getInt(Constants.END_TIME, 0);
        TimeRangFilter timeRangFilter = new TimeRangFilter(startTime, endTime);

        boolean isOpen = sharedPreferences.getBoolean(USE_TIME_RANGE, false);
        if (isOpen) {
            timeRangFilter.open();
        } else {
            timeRangFilter.close();
        }
        return timeRangFilter;
    }

}
