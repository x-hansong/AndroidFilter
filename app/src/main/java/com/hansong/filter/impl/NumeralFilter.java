package com.hansong.filter.impl;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.hansong.filter.app.DBOpenHelper;
import com.hansong.filter.core.AbsFilter;
import com.hansong.filter.core.IFilter;
import com.hansong.filter.core.MessageData;

import java.util.ArrayList;
import java.util.List;

import static com.hansong.filter.utils.Constants.*;

/**
 * 
 * 基于号码严格匹配的过滤器
 * @author boyliang
 *
 */
public final class NumeralFilter extends AbsFilter {
    private static final String TAG = NumeralFilter.class.getName();

    private ArrayList<String> numbers;
    private int mOpcode;

    private NumeralFilter(int opcode){
        mOpcode = opcode;
        numbers = new ArrayList<String>();
    }

    public ArrayList<String> getNumbers() {
        return numbers;
    }

    @Override
    public int onFiltering(MessageData data) {
        String phone = data.getString(MessageData.KEY_DATA);

        for(String number : numbers){
            if(number != null && number.equals(phone)){
                Log.d(TAG, "黑名单拦截：" + phone);
                return mOpcode;
            }
        }

        return IFilter.OP_SKIP;
    }

    public static NumeralFilter build(int mOpcode, Context context) {
        NumeralFilter numeralFilter = new NumeralFilter(mOpcode);
        List<String> numbers = numeralFilter.getNumbers();

        //加载配置
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILTER_PROS, Context.MODE_PRIVATE);
        boolean isOpen = sharedPreferences.getBoolean(USE_BLACK_LIST, false);
        if (isOpen) {
            numeralFilter.open();
        } else {
            numeralFilter.close();
        }

        // 从数据库加载黑名单
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(T_BLACK_LIST, new String[]{PHONE}, null, null, null, null,null);
        cursor.moveToFirst();
        Log.d(TAG, "数据库中已有黑名单记录：" + cursor.getCount());

        while (!cursor.isAfterLast()) {
            numbers.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return numeralFilter;

    }

}
