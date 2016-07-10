package com.hansong.filter.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.hansong.filter.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.hansong.filter.utils.Constants.*;

public class MainActivity extends Activity {

    public static Handler handler;

    private Button btnConfig;
    private ListView lvRecord;
    private LinkedList<Map<String, String>> records = new LinkedList<Map<String, String>>();

    private static final String TAG = MainActivity.class.getName();

    private DBOpenHelper dbOpenHelper;
    private ListAdapter adapter;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbOpenHelper = new DBOpenHelper(this, DB_NAME, null, DB_VERSION);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String phone = msg.getData().getString("phone");
                String date = msg.getData().getString("date");

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("phone", phone);
                map.put("date", date);

                records.addFirst(map);
                lvRecord.setAdapter(adapter);
                lvRecord.invalidate();
            }
        };

        // View引用初始化
        lvRecord = (ListView) findViewById(R.id.ls_history);
        btnConfig = (Button) findViewById(R.id.btn_config);

        //跳转到配置页面
        final Intent intent = new Intent(this, ConfigActivity.class);
        btnConfig.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        //数据绑定
        loadRecord();
        adapter = new SimpleAdapter(this, records, android.R.layout.simple_list_item_2, new String[]{"phone", "date"}, new int[]{android.R.id.text1, android.R.id.text2});
        lvRecord.setAdapter(adapter);

        //创建启动Service的Intent,以及Intent属性
        final Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.hansong.filter.app.FILTER_SERVICE");
        serviceIntent.setPackage(getPackageName());
        startService(serviceIntent);
    }

    private void loadRecord() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(T_RECORD, new String[] {PHONE, TIME}, null, null, null, null, "rid desc");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("phone", cursor.getString(0));
            map.put("date", cursor.getString(1));

            records.add(map);
            cursor.moveToNext();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

