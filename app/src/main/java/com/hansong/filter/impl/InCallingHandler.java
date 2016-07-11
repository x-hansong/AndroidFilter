package com.hansong.filter.impl;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.telephony.ITelephony;
import com.hansong.filter.app.DBOpenHelper;
import com.hansong.filter.app.MainActivity;
import com.hansong.filter.core.AbsHandler;
import com.hansong.filter.core.IFilter;
import com.hansong.filter.core.MessageData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.hansong.filter.utils.Constants.*;

/**
 * 来电 处理器， 主要对经由各种Filter判断之后的结果进行处理，比如挂断来电，通知栏提醒等等
 *
 * @author hansong
 */
public final class InCallingHandler extends AbsHandler {
    private static final String TAG = InCallingHandler.class.getName();
    private DBOpenHelper dbOpenHelper;

    public InCallingHandler(Context context) {
        dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 挂断电话 需要权限:android.permission.CALL_PHONE
     */
    private void endCall() {
        try {
           Class<?> aClass = Class.forName("android.os.ServiceManager");//通过反射找到ServiceManager
            Method method = aClass.getMethod("getService", String.class);//找到ServiceManager的静态方法getService
            IBinder b = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);//调用getService()方法，得到IBinder对象
            ITelephony service = ITelephony.Stub.asInterface(b);//得到TelephonyManager接口
            service.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(MessageData data) {
        int opcode = data.getInt(MessageData.KEY_OP);
        //如果需要拦截
        if (opcode == IFilter.OP_BLOCKED) {
            //挂断电话
            endCall();

            //拦截记录写入数据库
            String phone = data.getString(MessageData.KEY_DATA);
            String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                    Locale.CHINA).format(new Date());
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(PHONE, phone);
            contentValues.put(TIME, date);
            db.insert(T_RECORD, null, contentValues);

            //刷新activity
            if (MainActivity.handler != null) {
                Bundle bundle = new Bundle();
                bundle.putString("phone", phone);
                bundle.putString("date", date);
                Message msg = new Message();
                msg.what = 11;
                msg.setData(bundle);
                MainActivity.handler.sendMessage(msg);
            } else {
                Log.d(TAG, "主界面被杀死");
            }

            Log.d(TAG, "挂断电话：" + phone);
        }

    }
}
