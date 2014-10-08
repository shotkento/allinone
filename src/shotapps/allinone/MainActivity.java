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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String SENTENCE_TABLE = "sentence_tbl";
    private static final String WORD_TABLE = "word_tbl";
    private static final String IDIOM_TABLE = "idiom_tbl";
    private static final String TABLE_TRAINING = "training";
    private static final String[] COLUMNS = { "allinone._id", "allinone.day",
            "allinone.english_txt", "allinone.japanese_txt_order",
            "allinone.japanese_txt_normal", "training.count",
            "training.correct" };
    private final int DAY_MIN = 1;
    private final int DAY_MAX = 60;
    private final int DAY_PACE = 7;

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
                ArrayList<SentenceData> dataList = findData(dayStart, dayEnd,
                        random);
                if (dataList.size() == 0) {
                    Log.e(TAG, "Failed to create data list");
                    return;
                }

                Intent intent = new Intent(MainActivity.this,
                        TrainingActivity.class);
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

    private ArrayList<SentenceData> findData(int dayStart, int dayEnd,
            boolean random) {
        Log.d(TAG, "Start findData()");
        if (dayStart > dayEnd) {
            // スタート日の方が大きければ入れ替え
            int temp = dayStart;
            dayStart = dayEnd;
            dayEnd = temp;
        }
        // 日にちからidを特定
        int startId = (dayStart - 1) * DAY_PACE + 1;
        int endId = dayEnd * DAY_PACE;

        String[] day = new String[] { Integer.toString(dayStart),
                Integer.toString(dayEnd) };
        String orderBy = null;
        if (random) {
            // ランダムスイッチがONの場合
            orderBy = "RANDOM()";
        }

        final String sql = "select * from " + SENTENCE_TABLE
                + " where _id BETWEEN " + startId + " AND " + endId;

        ArrayList<SentenceData> dataList = new ArrayList<SentenceData>();
        // Cursor cs = mDb.query(TABLE_NAME, COLUMNS, "day BETWEEN ? AND ?",
        // day, null, null, orderBy);
        Cursor cs = mDb.rawQuery(sql, null);
        while (cs.moveToNext()) {
            SentenceData data = new SentenceData();
            data.id = cs.getInt(0);
            data.eng_sent = cs.getString(1);
            data.jpn_sent_order = cs.getString(2);
            data.jpn_sent_normal = cs.getString(3);
            data.count = cs.getInt(4);
            data.correct = cs.getInt(5);
            data.checked = cs.getInt(6);

            dataList.add(data);
        }
        cs.close();

        Log.d(TAG, "End findData()");
        return dataList;
    }

    public class SentenceData implements Serializable {
        int id;
        String eng_sent;
        String jpn_sent_order;
        String jpn_sent_normal;
        int correct;
        int count;
        int checked;
    }
}
