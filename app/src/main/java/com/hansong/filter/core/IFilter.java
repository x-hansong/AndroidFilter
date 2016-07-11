package com.hansong.filter.core;

/**
 * 数据过滤器，接口类型
 * @author hansong
 */
public interface IFilter {
	
	/**
	 * 拦截
	 */
	final int OP_BLOCKED = 0; 
	
	/**
	 * 跳过
	 */
	final int OP_SKIP = 1;
	
	/**
	 * 放行
	 */
	final int OP_PASS = 2;
	
	/**
	 * 过滤逻辑判断
	 * @param data
	 * @return
	 */
	int onFiltering(MessageData data);


	/**
	 * 是否开启着
	 * @return
     */
	boolean isOpen();

	/**
	 * 关闭过滤器
	 */
	void close();

	/**
	 * 开启过滤器
	 */
	void open();
}
