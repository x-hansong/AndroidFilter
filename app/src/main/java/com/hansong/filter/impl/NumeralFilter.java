package com.hansong.filter.impl;


import android.util.Log;
import com.hansong.filter.core.AbsFilter;
import com.hansong.filter.core.IFilter;
import com.hansong.filter.core.MessageData;

import java.util.Set;

/**
 * 
 * 基于号码严格匹配的过滤器
 * @author hansong
 *
 */
public final class NumeralFilter extends AbsFilter {
    private static final String TAG = NumeralFilter.class.getName();

    private Set<String> numbers;
    private int mOpcode;

    private NumeralFilter(){
    }

    public Set<String> getNumbers() {
        return numbers;
    }

    @Override
    public int onFiltering(MessageData data) {
        String phone = data.getString(MessageData.KEY_DATA);

        if (numbers.contains(phone)) {
            if (mOpcode == IFilter.OP_BLOCKED) {
                Log.d(TAG, "黑名单拦截：" + phone);
            } else {
                Log.d(TAG, "白名单放行：" + phone);
            }
            return mOpcode;
        } else {
            return IFilter.OP_SKIP;
        }
    }

    public void setmOpcode(int mOpcode) {
        this.mOpcode = mOpcode;
    }

    public void setNumbers(Set<String> numbers) {
        this.numbers = numbers;
    }

    public static class Builder {
        private NumeralFilter numeralFilter;
        public Builder() {
            numeralFilter = new NumeralFilter();
        }

        public Builder setOpcode(int opcode) {
            numeralFilter.setmOpcode(opcode);
            return this;
        }

        public Builder setNumber(Set<String> numbers) {
            numeralFilter.setNumbers(numbers);
            return this;
        }

        public Builder setStatus(boolean isOpen) {
            if (isOpen) {
                numeralFilter.open();
            } else {
                numeralFilter.close();
            }
            return this;
        }

        public NumeralFilter create() {
            return numeralFilter;
        }
    }

}
