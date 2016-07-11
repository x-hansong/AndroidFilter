package com.hansong.filter.impl;

import com.hansong.filter.core.AbsFilter;
import com.hansong.filter.core.IFilter;
import com.hansong.filter.core.MessageData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * 基于正则表达式的过滤器
 * @author hansong
 */
public final class RegexFilter extends AbsFilter {
	private Pattern mPattern;
	private int mOpcode;

	public RegexFilter(int opcode, String pattern) throws PatternSyntaxException {
		mPattern = Pattern.compile(pattern, 0);
		mOpcode = opcode;
	}
	

	@Override
	public int onFiltering(MessageData data) {
		String phone = data.getString(MessageData.KEY_DATA);
		Matcher m = mPattern.matcher(phone);
		
		if(m.find()){
			return mOpcode;
		}else{
			return IFilter.OP_SKIP;
		}
	}
}
