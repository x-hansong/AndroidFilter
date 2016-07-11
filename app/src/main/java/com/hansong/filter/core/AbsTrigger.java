package com.hansong.filter.core;

/**
 * 事件触发器，所有来电，短信接收都由这里产生
 * 抽象类
 * @author hansong
 */
public abstract class AbsTrigger {
	
	/**
	 * 触发监听器
	 * @author hansong
	 *
	 */
	interface ITriggerListener {
		void onMessageComing(MessageData data);
	}
	
	private ITriggerListener mListener;
	
	
	protected void notify(MessageData data) {
		
		if(mListener != null){
			mListener.onMessageComing(data);
		}
	}
	
	protected abstract void enable();
	
	protected abstract void disable();
	
	void setListener(ITriggerListener listener){
		mListener = listener;
	}
	
	ITriggerListener getListener(){
		return mListener;
	}
}
