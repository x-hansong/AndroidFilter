package com.hansong.filter.impl;


import com.hansong.filter.core.AbsFilter;
import com.hansong.filter.core.MessageData;

/**
 * 基于号码归属地的过滤器，走网络请求，所以需求时间，作为进阶课程内容
 * HTTP API: https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=1111111111
 * @author boyliang
 *
 */
public final class LocationFilter extends AbsFilter {
	

	@Override
	public int onFiltering(MessageData data) {
		// TODO Auto-generated method stub
		return 0;
	}

}
