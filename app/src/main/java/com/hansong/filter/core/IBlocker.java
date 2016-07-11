package com.hansong.filter.core;

/**
 * 拦截器，负责把Trigger, Filter和Handler三者关联起来 
 * @author hansong
 *
 */
public interface IBlocker {

    /**
     * 启动
     */
    void enable();

    /**
     * 关闭
     */
    void disable();

    /**
     * 获取对应的过滤器
     * @param name
     * @return
     */
    IFilter getFilter(String name);
}
