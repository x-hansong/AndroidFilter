package com.hansong.filter.core;

import android.util.SparseArray;
import android.util.SparseIntArray;

/**
 * 数据传递载体，主要用于Trigger, Filter和Handler之间的数据传递。本类非线程安全，使用时需要注意
 * @author hansong
 */
public final class MessageData{
	public static final String KEY_OP	 	= "key_op";
	public static final String KEY_DATA 	= "key_data";
	
	static class StringKeySparseIntArray extends SparseIntArray{
		
		public int get(String key, int defalut){
			return get(key.hashCode(), defalut);
		}
		
		public int get(String key){
			return get(key, -1);
		}
		
		public int set(String key, int val) {
			int old = get(key);
			put(key.hashCode(), val);
			return old;
		}
	}
	
	static class StringKeySparseArray<T> extends SparseArray<T>{
		
		public T get(String key, T defval) {
			return get(key.hashCode(), defval);
		}

		public T get(String key) {
			return get(key, null);
		}
		
		public T set(String key, T val){
			T old = get(key);
			put(key.hashCode(), val);
			return old;
		}
	}
	
	private StringKeySparseIntArray mIntDatas = new StringKeySparseIntArray();
	private StringKeySparseArray<String> mStringDatas = new StringKeySparseArray<String>();
	
	/**
	 * 获取key获取整数，如果key不存在，则返回defval
	 * @param key 
	 * @param defval
	 * @return
	 */
	public int getInt(String key, int defval){
		return mIntDatas.get(key, defval);
	}
	
	/**
	 * getInt的一个简化版本
	 * @param key
	 * @return
	 */
	public int getInt(String key){
		return mIntDatas.get(key);
	}
	
	public int setInt(String key, int val){
		return mIntDatas.set(key, val);
	}
	
	public String getString(String key, String defval){
		return mStringDatas.get(key.hashCode(), defval);
	}
	
	public String getString(String key){
		return getString(key, null);
	}
	
	public String setString(String key, String val){
		return mStringDatas.set(key, val);
	}
}
