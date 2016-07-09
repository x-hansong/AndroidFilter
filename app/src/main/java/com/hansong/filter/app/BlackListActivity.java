package com.hansong.filter.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.hansong.filter.R;
import com.hansong.filter.core.IBlocker;
import com.hansong.filter.impl.NumeralFilter;
import com.hansong.filter.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.content.SharedPreferences.Editor;
import static com.hansong.filter.utils.Constants.*;

public class BlackListActivity extends Activity {

    public static String TAG = BlackListActivity.class.getName();

    private Switch aSwitch;
    private Button btnAdd;
    private EditText etNumber;
    private ListView lvNumber;
    private DBOpenHelper dbOpenHelper;
    private SharedPreferences sharedPreferences;
    private ArrayAdapter adapter;
    private List<String> blackList;
    private NumeralFilter numeralFilter;
    private AlertDialog alert;
    private AlertDialog.Builder builder;
    private String selectedPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);

        dbOpenHelper = new DBOpenHelper(this, DB_NAME, null, DB_VERSION);

        sharedPreferences = this.getSharedPreferences(FILTER_PROS, Context.MODE_PRIVATE);

        setupFilter();

        initView();

        initData();
    }

    private void setupFilter() {
        FilterApp filterApp = (FilterApp) getApplication();
        IBlocker iBlocker = filterApp.getiBlocker();
        numeralFilter = (NumeralFilter) iBlocker.getFilter(Constants.BLACK_LIST_FILTER);
    }

    private void initView() {
        aSwitch = (Switch) findViewById(R.id.sw_use_black_list);
        btnAdd = (Button) findViewById(R.id.btn_add);
        etNumber = (EditText) findViewById(R.id.et_number);
        lvNumber = (ListView) findViewById(R.id.lv_number);

        btnAdd.setOnClickListener(new AddPhoneListener());

        aSwitch.setChecked(numeralFilter.isOpen());
        aSwitch.setOnCheckedChangeListener(new SwitchListener());

        builder = new AlertDialog.Builder(this);
        alert = builder.setTitle("提示：")
                .setMessage("是否删除该号码？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "删除号码：" + selectedPhone);
                        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                        db.delete(T_BLACK_LIST, "phone=?", new String[] {selectedPhone} );

                        blackList.remove(selectedPhone);
                        adapter.notifyDataSetChanged();
                    }
                }).create();

        lvNumber.setOnItemClickListener(new ItemClickListener());
    }

    /**
     * 初始化数据
     */
    private void initData() {

        blackList = new ArrayList<String>(numeralFilter.getNumbers().size());
        blackList.addAll(numeralFilter.getNumbers());
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, blackList);
        lvNumber.setAdapter(adapter);
    }

    class AddPhoneListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
            String phone = etNumber.getText().toString();
            if (phone.length() > 0) {
                //如果数据库中没有该号码就加入数据库
                Cursor cursor = db.query(T_BLACK_LIST, null, PHONE + "=?", new String[]{phone}, null,null, null);
                if (cursor.getCount() == 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(PHONE, phone);
                    db.insert(T_BLACK_LIST, null, contentValues);

                    blackList.add(phone);
                    numeralFilter.getNumbers().add(phone);

                    //刷新listview
                    adapter.notifyDataSetChanged();
                }
                cursor.close();
            }
            db.close();
        }
    }

    class ItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String phone = ((TextView)view).getText().toString();
            selectedPhone = phone;
            alert.show();
        }
    }

    class SwitchListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(USE_BLACK_LIST, isChecked);
            editor.apply();

            Log.d(TAG, "黑名单配置开关：" + isChecked);

            if (isChecked) {
                numeralFilter.open();
            } else {
                numeralFilter.close();
            }
        }
    }
}
