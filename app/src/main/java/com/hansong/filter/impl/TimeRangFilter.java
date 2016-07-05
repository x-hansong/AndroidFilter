package com.hansong.filter.impl;

import com.hansong.filter.core.AbsFilter;
import com.hansong.filter.core.IFilter;
import com.hansong.filter.core.MessageData;

import java.util.Calendar;


/**
 * 按时段过滤
 * @author boyliang
 *
 */
public final class TimeRangFilter extends AbsFilter {
	private int mStartHour;
	private int mEndHour;

	public TimeRangFilter(int starthour, int endhour){
		mStartHour = starthour;
		mEndHour = endhour;
	}
	
	@Override
	public int onFiltering(MessageData data) {

		Calendar now = Calendar.getInstance();
		int current_hour = now.get(Calendar.HOUR_OF_DAY);
		
		if(current_hour >= mStartHour && current_hour <= mEndHour){
			return IFilter.OP_BLOCKED;
		}else{
			return IFilter.OP_SKIP;
		}
	}


}
