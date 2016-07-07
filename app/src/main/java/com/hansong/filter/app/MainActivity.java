package com.hansong.filter.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.hansong.filter.R;
import com.hansong.filter.core.*;
import com.hansong.filter.impl.NumeralFilter;
import com.hansong.filter.impl.TimeRangFilter;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.hansong.filter.utils.Constants.BLACK_LIST_FILTER;
import static com.hansong.filter.utils.Constants.TIME_RANGE_FILTER;


public class MainActivity extends Activity {

    /**
     * DEMO专用的Trigger，主要用于模拟器来电事件
     *
     * @author boyliang
     */
    class DemoTrigger extends AbsTrigger {
        private boolean mState = false;

        @Override
        protected void enable() {
            mState = true;
        }

        @Override
        protected void disable() {
            mState = false;
        }

        public void emulateInComingCall(String phone) {
            if (mState) {
                MessageData data = new MessageData();
                data.setString(MessageData.KEY_DATA, phone);
                notify(data);
            }
        }
    }

    /**
     * DEMO专用的Handler，主要用于更新ListView列表数据
     *
     * @author boyliang
     */
    class DemoHandler extends AbsHandler {

        public void handle(MessageData data) {
            int opcode = data.getInt(MessageData.KEY_OP);
            String phone = data.getString(MessageData.KEY_DATA);

            // 刷新数据列表
            String phonestr = String.format("(%s)%s",
                    (opcode == IFilter.OP_BLOCKED) ? "拦截" :
                            ((opcode == IFilter.OP_PASS) ? "放行" : "跳过"), phone);
            String datestr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                    Locale.CHINA).format(new Date());

            Bundle bundle = new Bundle();
            bundle.putString("phone", phonestr);
            bundle.putString("date", datestr);

            Message msg = mUIHandler.obtainMessage();
            msg.what = 11;
            msg.setData(bundle);

            msg.sendToTarget();
        }
    }


    private Button mBTEmulate;
    private Button mBTConfig;
    private TextView mTVPhone;
    private ListView mLVHistory;
    private LinkedList<Map<String, String>> mHistoryData = new LinkedList<Map<String, String>>();
    private IBlocker mBlocker;
    private AbsTrigger mTrigger = new DemoTrigger();
    private AbsHandler mHandler = new DemoHandler();

    private ListAdapter mAdapter;
    private Handler mUIHandler;
    private FilterApp filterApp;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View引用初始化
        mBTEmulate = (Button) findViewById(R.id.bt_emulate);
        mTVPhone = (TextView) findViewById(R.id.ed_phonenumber);
        mLVHistory = (ListView) findViewById(R.id.ls_history);
        mBTConfig = (Button) findViewById(R.id.btn_config);

        filterApp = (FilterApp) getApplication();

        //回调设置
        mBTEmulate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mTVPhone.getText().toString();
                mTVPhone.setText("");
                ((DemoTrigger) mTrigger).emulateInComingCall(input);
            }
        });

        //跳转到配置页面
        final Intent intent = new Intent(this, ConfigActivity.class);
        mBTConfig.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        //数据绑定
        mAdapter = new SimpleAdapter(this, mHistoryData, android.R.layout.simple_list_item_2, new String[]{"phone", "date"}, new int[]{android.R.id.text1, android.R.id.text2});
        mLVHistory.setAdapter(mAdapter);

        //Blocker设置
        setupBlocker();

        //UI刷新线程
        mUIHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                String phonestr = msg.getData().getString("phone");
                String datestr = msg.getData().getString("date");

                HashMap<String, String> item = new HashMap<String, String>();
                item.put("phone", phonestr);
                item.put("date", datestr);

                mHistoryData.addFirst(item);
                mLVHistory.setAdapter(mAdapter);
                mLVHistory.invalidate();

                // 通知栏提示
                Context context = MainActivity.this;
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(context);
                Notification notification = builder
                        .setContentTitle(phonestr)
                        .setContentText(datestr)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .build();

                notificationManager.notify(22, notification);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupBlocker() {
        BlockerBuilder builder = new BlockerBuilder();

        mBlocker = builder
                .setTrigger(mTrigger)
                .setHandler(mHandler)
//                .addFilters(BLACK_LIST_FILTER, new NumeralFilter(IFilter.OP_PASS, "95555", "95588"))         //实现白名单放行
                .addFilters(BLACK_LIST_FILTER, NumeralFilter.build(IFilter.OP_BLOCKED, this))   //实现黑名单放行
                .addFilters(TIME_RANGE_FILTER, TimeRangFilter.build(this)) //前缀拦截
//				.addFilters(new LocationFilter()) //实现归属地拦截， 进阶课程的内容
//				.addFilters(new SystemContactFilter()) //系统联系人过滤， 进阶课程的内容
                .create();

        mBlocker.enable();

        filterApp.setiBlocker(mBlocker);
    }

}

