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

public abstract class BaseActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String SENTENCE_TABLE = "sentence_tbl";
    private static final String WORD_TABLE = "word_tbl";
    private static final String IDIOM_TABLE = "idiom_tbl";
    private static final String ORDER_RANDOM = "RANDOM()";
    private static final String ORDER_ASC = "_id ASC";
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

        // Sentenceボタン設定
        Button SentenceBtn = (Button) findViewById(R.id.startButton);
        Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        SentenceBtn.setTypeface(tf);
        SentenceBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int dayStart = mDayStart.getValue();
                int dayEnd = mDayEnd.getValue();
                boolean random = mRandomSwt.isChecked();
                ArrayList<SentenceData> dataList = findData(dayStart, dayEnd,
                        random);
                if (dataList.size() == 0) {
                    Log.e(TAG, "SentenceData is not found!!");
                    return;
                }

                Intent intent = new Intent(BaseActivity.this,
                        SentenceActivity.class);
                intent.putExtra("data", dataList);
                startActivity(intent);
                Log.d(TAG, "Start SentenceActivity");
            }
        });

        // Wordボタン設定
        Button wordBtn = (Button) findViewById(R.id.wordTestBtn);
        wordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this,
                        WordActivity.class);
                startActivity(intent);
            }
        });

        // WordListボタン
        Button wordListBtn = (Button) findViewById(R.id.wordListBtn);
        wordListBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BaseActivity.this,
                        WordListActivity.class);
                startActivity(intent);
                Log.d(TAG, "Start WordListActivity");
            }
        });

        setupDatabase();
    }

    @Override
    protected void onDestroy() {
        if (mDb.isOpen()) {
            mDb.close();
        }
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

        String[] id = new String[] { Integer.toString(startId),
                Integer.toString(endId) };
        String orderBy = null;
        if (random) {
            // ランダムスイッチがONの場合
            orderBy = ORDER_RANDOM;
        }
        //
        // final String sql = "select * from " + SENTENCE_TABLE
        // + " where _id BETWEEN " + startId + " AND " + endId;

        ArrayList<SentenceData> dataList = new ArrayList<SentenceData>();
        Cursor cs = mDb.query(SENTENCE_TABLE, null, "_id BETWEEN ? AND ?", id,
                null, null, orderBy);
        // Cursor cs = mDb.rawQuery(sql, null);
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

        Log.d(TAG, "End findData() dataList.size() is " + dataList.size());
        return dataList;
    }

    private ArrayList<WordData> findWordData() {
        ArrayList<WordData> dataList = new ArrayList<WordData>();
        Cursor cs = mDb.query(WORD_TABLE, null, null, null, null, null,
                ORDER_ASC);

        while (cs.moveToNext()) {
            WordData data = new WordData();
            data.id = cs.getInt(0);
            data.eng_word = cs.getString(1);
            data.jpn_word = cs.getString(2);
            data.sent_num1 = cs.getInt(3);
            data.sent_num2 = cs.getInt(4);
            data.count = cs.getInt(5);
            data.correct = cs.getInt(6);
            data.checked = cs.getInt(7);

            dataList.add(data);
        }
        cs.close();

        Log.d(TAG, "End findWordData() dataList.size() is " + dataList.size());
        return dataList;
    }

    public static class SentenceData implements Serializable {
        int id;
        String eng_sent;
        String jpn_sent_order;
        String jpn_sent_normal;
        int correct;
        int count;
        int checked;
    }

    public static class WordData implements Serializable {
        int id;
        String eng_word;
        String jpn_word;
        int sent_num1;
        int sent_num2;
        int correct;
        int count;
        int checked;
    }

    public static class IdiomData implements Serializable {
        int id;
        String eng_idiom;
        String jpn_idiom;
        int sent_num1;
        int sent_num2;
        int correct;
        int count;
        int checked;
    }
}
