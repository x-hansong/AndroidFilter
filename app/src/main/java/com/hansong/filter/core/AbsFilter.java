package com.hansong.filter.core;

/**
 * Created by xhans on 2016/7/3 0003.
 */
public abstract class AbsFilter implements IFilter{
    protected boolean isOpen = false;

    @Override
    public void open() {
        isOpen = true;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void close() {
        isOpen = false;
    }
}
