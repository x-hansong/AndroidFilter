package com.hansong.filter.app;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.hansong.filter.core.BlockerBuilder;
import com.hansong.filter.core.IBlocker;
import com.hansong.filter.core.IFilter;
import com.hansong.filter.impl.*;

import java.util.HashSet;
import java.util.Set;

import static com.hansong.filter.utils.Constants.*;

/**
 * Created by xhans on 2016/7/9 0009.
 */
public class FilterService extends Service{

    private static final String TAG = FilterService.class.getName();
    private TelephonyManager tm;
    private SharedPreferences sharedPreferences;
    private DBOpenHelper dbOpenHelper;
    private FilterApp filterApp;
    private IBlocker iBlocker;
    private InCallingTrigger inCallingTrigger;
    private InCallingHandler inCallingHandler;
    private PhoneStateListener listener;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences  = this.getSharedPreferences(FILTER_PROS, Context.MODE_PRIVATE);
        dbOpenHelper = new DBOpenHelper(this, DB_NAME, null, DB_VERSION);
        filterApp = (FilterApp) getApplication();
        inCallingHandler = new InCallingHandler(this);
        inCallingTrigger = new InCallingTrigger();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

       listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:// 如果是来电的时候
                        Log.d(TAG, "来电：" + incomingNumber);
                        inCallingTrigger.fireOnCall(incomingNumber);
                        break;
                    default:
                        break;
                }
            }
        };

        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        setupBlocker();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    private void setupBlocker() {
        BlockerBuilder builder = new BlockerBuilder();

        NumeralFilter blackFilter = new NumeralFilter.Builder()
                .setOpcode(IFilter.OP_BLOCKED)
                .setNumber(loadBlackList())
                .setStatus(sharedPreferences.getBoolean(USE_BLACK_LIST, false))
                .create();

        SystemContactFilter systemContactFilter = new SystemContactFilter.Builder()
                .setStatus(sharedPreferences.getBoolean(USE_CONTACT, false))
                .setContact(loadContacts())
                .create();

        LocationFilter locationFilter = new LocationFilter.Builder()
                .setOpcode(IFilter.OP_BLOCKED)
                .setProvince(loadProvince())
                .setStatus(sharedPreferences.getBoolean(USE_LOCATION, false))
                .create();

        int startTime = sharedPreferences.getInt(START_TIME, 0);
        int endTime = sharedPreferences.getInt(END_TIME, 0);
        TimeRangFilter timeRangFilter = new TimeRangFilter.Builder()
                .setTimeRange(startTime, endTime)
                .setStatus(sharedPreferences.getBoolean(USE_TIME_RANGE, false))
                .create();


        iBlocker = builder
                .setTrigger(inCallingTrigger)
                .setHandler(inCallingHandler)
                .addFilters(CONTACT_FILTER, systemContactFilter)
                .addFilters(BLACK_LIST_FILTER, blackFilter)
                .addFilters(TIME_RANGE_FILTER, timeRangFilter)
                .addFilters(LOCATION_FILTER, locationFilter)
                .create();

        iBlocker.enable();

        filterApp.setiBlocker(iBlocker);
    }

    private Set<String> loadBlackList() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(T_BLACK_LIST, new String[]{PHONE}, null, null, null, null,null);
        cursor.moveToFirst();
        Log.d(TAG, "数据库中已有黑名单记录：" + cursor.getCount());

        Set<String> numbers = new HashSet<String>();
        while (!cursor.isAfterLast()) {
            numbers.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return numbers;
    }

    private Set<String> loadContacts() {
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        Set<String> numbers = new HashSet<String>();

        Log.d(TAG, "加载联系人记录：" + cursor.getCount());
        //向下移动光标
        while(cursor.moveToNext()) {
            String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.d(TAG, phone);
            //格式化手机号
            phone = phone.replace("-","");
            phone = phone.replace(" ","");
            numbers.add(phone);
        }
        return numbers;
    }

    private Set<String> loadProvince() {
        Set<String> provinces = new HashSet<String>();
        for (String province : PROVINCES) {
            if (sharedPreferences.getBoolean(province, false)) {
                provinces.add(province);
            }
        }
        return provinces;
    }

}
