package com.hansong.filter.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 拦截器构造者，用于Blocker的配置及构造
 * @author hansong
 */
public final class BlockerBuilder {



    /**
     * IBlocker的内部实现，隐藏Blocker的实现细节
     * @author hansong
     *
     */
    static final class BlockerImpl implements IBlocker, AbsTrigger.ITriggerListener{
        private AbsTrigger mTrigger;
        private ArrayList<IFilter> mFilters = new ArrayList<IFilter>();
        private LinkedHashMap<String, IFilter> filters = new LinkedHashMap<String, IFilter>();
        private AbsHandler mHandler;

        @Override
        public void enable() {
            if(mTrigger != null){
                mTrigger.enable();
            }else{
                throw new RuntimeException("mTrigger must be set.");
            }
        }

        @Override
        public void disable() {
            if(mTrigger != null){
                mTrigger.disable();
            }else{
                throw new RuntimeException("mTrigger must be set.");
            }
        }

        @Override
        public IFilter getFilter(String name) {
            return filters.get(name);
        }

        @Override
        public void onMessageComing(MessageData data) {
            int opcode = IFilter.OP_PASS;

            for(IFilter filter : filters.values()){
                if (filter.isOpen()) {
                    opcode = filter.onFiltering(data);

                    if (opcode == IFilter.OP_PASS || opcode == IFilter.OP_BLOCKED) {
                        break;
                    }
                }
            }

            data.setInt(MessageData.KEY_OP, opcode);

            if(mHandler != null){
                mHandler.handle(data);
            }
        }

        public void addFilters(String name, IFilter filter){
            filters.put(name, filter);
        }

        public void setTrigger(AbsTrigger trigger){
            if(mTrigger != null){
                mTrigger.setListener(null);
            }

            mTrigger = trigger;

            if(mTrigger != null){
                mTrigger.setListener(this);
            }
        }

        public void setHandler(AbsHandler handler){
            mHandler = handler;
        }
    }

    private BlockerImpl mBlocker;

    public BlockerBuilder(){
        mBlocker = new BlockerImpl();
        mBlocker.setTrigger(new AbsTrigger() {

            @Override
            protected void enable() {
                //DO NOTHING
            }

            @Override
            protected void disable() {
                //DO NOTHING
            }
        });
        mBlocker.setHandler(new AbsHandler(){

        });

    }

    public IBlocker create(){
        return mBlocker;
    }

    /**
     * 添加 过滤器
     * @param filter
     */
    public BlockerBuilder addFilters(String name, IFilter filter){
        mBlocker.addFilters(name, filter);
        return this;
    }

    /**
     * 配置 触发器
     * @param trigger
     */
    public BlockerBuilder setTrigger(AbsTrigger trigger){
        mBlocker.setTrigger(trigger);
        return this;
    }

    /**
     * 配置 处理器
     * @param handler
     */
    public BlockerBuilder setHandler(AbsHandler handler){
        mBlocker.setHandler(handler);
        return this;
    }
}
