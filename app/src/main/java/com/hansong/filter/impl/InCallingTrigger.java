package com.hansong.filter.impl;


import com.hansong.filter.core.AbsTrigger;
import com.hansong.filter.core.MessageData;

/**
 * 来电触发器，主要用于捕获来电事件， 作为进阶课程内容
 * @author boyliang
 *
 */
public final class InCallingTrigger extends AbsTrigger {
    private boolean state = false;

    @Override
    protected void enable() {
        state = true;
    }

    @Override
    protected void disable() {
        state = false;
    }

    public void fireOnCall(String phone) {
        if (state) {
            MessageData data = new MessageData();
            data.setString(MessageData.KEY_DATA, phone);
            notify(data);
        }
    }
}
