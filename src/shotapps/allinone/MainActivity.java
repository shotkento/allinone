package shotapps.allinone;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TABLE_NAME = "allinone";
    private static final String TABLE_TRAINING = "training";
    private static final String[] COLUMNS = { "allinone._id", "allinone.day",
            "allinone.english_txt", "allinone.japanese_txt_order",
            "allinone.japanese_txt_normal", "training.count",
            "training.correct" };
    private final int DAY_MIN = 1;
    private final int DAY_MAX = 60;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDb;
    private NumberPicker mDayStart;
    private NumberPicker mDayEnd;
    private Switch mRandomSwt;
    private TextView mSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        this.getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // NumberPickerの設定
        mDayStart = (NumberPicker) findViewById(R.id.day_start);
        mDayStart.setMinValue(DAY_MIN);
        mDayStart.setMaxValue(DAY_MAX);
        mDayStart
                .setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mDayEnd = (NumberPicker) findViewById(R.id.day_end);
        mDayEnd.setMinValue(DAY_MIN);
        mDayEnd.setMaxValue(DAY_MAX);
        mDayEnd.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mSecret = (TextView) findViewById(R.id.secret);
        mSecret.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDayEnd.setValue(mDayStart.getValue());
            }
        });
        // Randomスイッチ
        mRandomSwt = (Switch) findViewById(R.id.random_switch);

        // Startボタン設定
        Button startBtn = (Button) findViewById(R.id.startButton);
        Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        startBtn.setTypeface(tf);
        startBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int dayStart = mDayStart.getValue();
                int dayEnd = mDayEnd.getValue();
                boolean random = mRandomSwt.isChecked();
                ArrayList<Data> dataList = findData(dayStart, dayEnd, random);
                if (dataList.size() == 0) {
                    return;
                }

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),
                        shotapps.allinone.TrainingActivity.class);
                intent.putExtra("data", dataList);
                startActivity(intent);
            }
        });

        // Wordテストボタン設定
        Button wordBtn = (Button) findViewById(R.id.wordTestBtn);
        wordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        WordActivity.class);
                startActivity(intent);
            }
        });

        setupDatabase();
    }

    @Override
    protected void onDestroy() {
        mDb.close();
        super.onDestroy();
    }

    private void setupDatabase() {
        mDatabaseHelper = new DatabaseHelper(this);
        try {
            mDatabaseHelper.createDatabase();
            mDb = mDatabaseHelper.openDatabase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

    private ArrayList<Data> findData(int dayStart, int dayEnd, boolean random) {
        if (dayStart > dayEnd) {
            int temp = dayStart;
            dayStart = dayEnd;
            dayEnd = temp;
        }
        String[] day = new String[] { Integer.toString(dayStart),
                Integer.toString(dayEnd) };
        String orderBy = null;
        if (random) {
            orderBy = "RANDOM()";
        }

        final String sql = "select allinone._id, allinone.day, allinone.english_txt,"
                + "allinone.japanese_txt_order, allinone.japanese_txt_normal, training.count, training.correct"
                + " from "
                + TABLE_NAME
                + " inner join "
                + TABLE_TRAINING
                + " ON "
                + TABLE_NAME
                + "._id = "
                + TABLE_TRAINING
                + "._id"
                + " where day BETWEEN " + dayStart + " AND " + dayEnd;

        ArrayList<Data> dataList = new ArrayList<Data>();
        // Cursor cs = mDb.query(TABLE_NAME, COLUMNS, "day BETWEEN ? AND ?",
        // day, null, null, orderBy);
        Cursor cs = mDb.rawQuery(sql, null);
        while (cs.moveToNext()) {
            Data data = new Data();
            data.id = cs.getInt(0);
            data.day = cs.getInt(1);
            data.english_txt = cs.getString(2);
            data.japanese_txt_order = cs.getString(3);
            data.japanese_txt_normal = cs.getString(4);
            data.count = cs.getInt(5);
            data.correct = cs.getInt(6);

            dataList.add(data);
        }
        cs.close();
        return dataList;
    }

    public static class Data implements Serializable {
        int id;
        int day;
        String english_txt;
        String japanese_txt_order;
        String japanese_txt_normal;
        int count;
        int correct;
    }

}
