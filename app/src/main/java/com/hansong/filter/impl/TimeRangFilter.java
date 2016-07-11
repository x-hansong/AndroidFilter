package com.hansong.filter.impl;

import android.util.Log;
import com.hansong.filter.core.AbsFilter;
import com.hansong.filter.core.IFilter;
import com.hansong.filter.core.MessageData;

import java.util.Calendar;


/**
 * 按时段过滤
 * @author hansong
 *
 */
public final class TimeRangFilter extends AbsFilter {


    public static String TAG = TimeRangFilter.class.getName();
    private int startHour;
    private int endHour;

    private TimeRangFilter(){
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    @Override
    public int onFiltering(MessageData data) {

        Calendar now = Calendar.getInstance();
        int current_hour = now.get(Calendar.HOUR_OF_DAY);

        String phone = data.getString(MessageData.KEY_DATA);
        if(current_hour >= startHour && current_hour <= endHour){
            Log.d(TAG, "时间拦截：" + phone);
            return IFilter.OP_BLOCKED;
        }else{
            return IFilter.OP_SKIP;
        }
    }

    public static class Builder {
        private TimeRangFilter timeRangFilter;

        public Builder() {
            timeRangFilter = new TimeRangFilter();
        }

        public Builder setTimeRange(int startHour, int endHour) {
            timeRangFilter.setStartHour(startHour);
            timeRangFilter.setEndHour(endHour);
            return this;
        }

        public Builder setStatus(boolean isOpen) {
            if (isOpen) {
                timeRangFilter.open();
            } else {
                timeRangFilter.close();
            }
            return this;
        }

        public TimeRangFilter create() {
            return timeRangFilter;
        }
    }

}
