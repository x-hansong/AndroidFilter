package com.hansong.filter.impl;


import android.util.Log;
import com.hansong.filter.core.AbsFilter;
import com.hansong.filter.core.MessageData;
import com.hansong.filter.utils.JsonUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 基于号码归属地的过滤器，走网络请求，所以需求时间，作为进阶课程内容
 * HTTP API: https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=1111111111
 *
 * @author boyliang
 */
public final class LocationFilter extends AbsFilter {

    private static final String TAG = LocationFilter.class.getName();
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private Set<String> provinces;
    private OkHttpClient client = new OkHttpClient();
    private int opCode;

    private LocationFilter() {
    }

    @Override
    public int onFiltering(MessageData data) {
        final String phone = data.getString(MessageData.KEY_DATA);

        Future<Integer> future = threadPool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Request request = new Request.Builder()
                .url("https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=" + phone)
                .build();
                Response response = client.newCall(request).execute();
                String body = response.body().string();
                Log.d(TAG, body);
                String[] strs = body.split("=");
                Map<String, String> map = JsonUtils.decode(strs[1], Map.class);
                if (map != null) {
                    String pro = map.get("province");
                    Log.d(TAG, "来电归属地为：" + pro);
                    if (pro != null && provinces.contains(pro)) {
                        Log.d(LocationFilter.class.getName(),
                                String.format("拦截归属地为%s的号码%s", pro, phone));
                        return opCode;
                    }
                }
                return OP_SKIP;
            }
        });
        try {
            //等待查询结果
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return OP_SKIP;
    }

    public Set<String> getProvinces() {
        return provinces;
    }

    public void setProvinces(Set<String> provinces) {
        this.provinces = provinces;
    }

    public int getOpCode() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public static class Builder {
        private LocationFilter locationFilter;

        public Builder() {
            locationFilter = new LocationFilter();
        }

        public Builder setOpcode(int opcode) {
            locationFilter.setOpCode(opcode);
            return this;
        }

        public Builder setProvince(Set<String> province) {
            locationFilter.setProvinces(province);
            return this;
        }

        public Builder setStatus(boolean isOpen) {
            if (isOpen) {
                locationFilter.open();
            } else {
                locationFilter.close();
            }
            return this;
        }

        public LocationFilter create() {
            return locationFilter;
        }
    }
}
