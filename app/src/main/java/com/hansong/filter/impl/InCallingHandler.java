package com.hansong.filter.impl;


import com.hansong.filter.core.AbsHandler;
import com.hansong.filter.core.MessageData;

/**
 * 来电 处理器， 主要对经由各种Filter判断之后的结果进行处理，比如挂断来电，通知栏提醒等等
 * @author boyliang
 *
 */
public final class InCallingHandler extends AbsHandler {
	
	@Override
	public void handle(MessageData data) {
		// TODO Auto-generated method stub 
		super.handle(data);
	}
}
