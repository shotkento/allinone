package shotapps.allinone;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import shotapps.allinone.data.MyApplication;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public abstract class BaseActivity extends FragmentActivity {
    private static final String SELECT = "_id BETWEEN ? AND ?";
    private static final String WORD_SELECT = "sent_num1 BETWEEN ? AND ? OR (sent_num2 BETWEEN ? AND ?)";
    private static final String TAG = "BaseActivity";
    protected static final String SENTENCE_TABLE = "sentence_tbl";
    protected static final String WORD_TABLE = "word_tbl";
    protected static final String IDIOM_TABLE = "idiom_tbl";
    protected static final String ORDER_RANDOM = "RANDOM()";
    protected static final String ORDER_ASC = "_id ASC";
    private static final String[] COLUMNS = { "allinone._id", "allinone.day",
            "allinone.english_txt", "allinone.japanese_txt_order",
            "allinone.japanese_txt_normal", "training.count",
            "training.correct" };
    private final int DAY_MIN = 1;
    private final int DAY_MAX = 60;
    private final int DAY_PACE = 7;

    protected DatabaseHelper databaseHelper;
    protected SQLiteDatabase db;
    protected MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myApplication = (MyApplication) this.getApplication();

        setupDatabase();
    }

    @Override
    protected void onDestroy() {
        if (db.isOpen()) {
            db.close();
        }
        super.onDestroy();
    }

    private void setupDatabase() {
        databaseHelper = new DatabaseHelper(this);
        try {
            databaseHelper.createDatabase();
            db = databaseHelper.openDatabase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

    protected ArrayList<SentenceData> findData(int dayStart, int dayEnd,
            String orderBy) {
        Log.d(TAG, "Start findData()");

        String[] id = getIdScope(dayStart, dayEnd);

        ArrayList<SentenceData> dataList = new ArrayList<SentenceData>();
        Cursor cs = db.query(SENTENCE_TABLE, null, SELECT, id,
                null, null, orderBy);
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

    protected ArrayList<WordData> findWordData(String table, int dayStart, int dayEnd, String orderBy) {

        String[] idScope = getIdScope(dayStart, dayEnd);
        String selection = WORD_SELECT;
        if (idScope == null) {
            selection = null;
        } else {
            Log.d(TAG, "idScope0 = " + idScope[0] + " idScope1 = " + idScope[1]);
            String[] temp = {idScope[0], idScope[1], idScope[0], idScope[1]};
            idScope = temp;
        }

        ArrayList<WordData> wordDataList = new ArrayList<WordData>();
        Cursor cs = db.query(table, null, selection, idScope,
                null, null, orderBy);

        while (cs.moveToNext()) {
            WordData data = new WordData();
            data.setId(cs.getInt(0));
            data.setEngWord(cs.getString(1));
            data.setJpnWord(cs.getString(2));
            data.setSentNum1(cs.getInt(3));
            data.setSentNum2(cs.getInt(4));
            data.setCount(cs.getInt(5));
            data.setCorrect(cs.getInt(6));
            data.setChecked(cs.getInt(7));

            wordDataList.add(data);
        }
        cs.close();

        Log.d(TAG, "End findWordData() wordDataList.size() is " + wordDataList.size());
        return wordDataList;
    }

    protected String checkRandom(boolean random) {
        String orderBy = null;
        if (random) {
            // ランダムスイッチがONの場合
            orderBy = ORDER_RANDOM;
        }
        return orderBy;
    }

    private String[] getIdScope(int dayStart, int dayEnd) {
        if (dayStart == 0 || dayEnd == 0) {
            return null;
        }
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
        return id;
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
        private int id;
        private String engWord;
        private String jpnWord;
        private int sentNum1;
        private int sentNum2;
        private int correct;
        private int count;
        private boolean checked;

        public int getId() {
            return id;
        }
        public String getEngWord() {
            return engWord;
        }
        public String getJpnWord() {
            return jpnWord;
        }
        public int getSentNum1() {
            return sentNum1;
        }
        public int getSentNum2() {
            return sentNum2;
        }
        public int getCorrect() {
            return correct;
        }
        public int getCount() {
            return count;
        }
        public boolean getChecked() {
            return checked;
        }
        public void setId(int id) {
            this.id = id;
        }
        public void setEngWord(String engWord) {
            this.engWord = engWord;
        }
        public void setJpnWord(String jpnWord) {
            this.jpnWord = jpnWord;
        }
        public void setSentNum1(int sentNum1) {
            this.sentNum1 = sentNum1;
        }
        public void setSentNum2(int sentNum2) {
            this.sentNum2 = sentNum2;
        }
        public void setCorrect(int correct) {
            this.correct = correct;
        }
        public void setCount(int count) {
            this.count = count;
        }
        public void setChecked(int checked) {
            if (checked == 0) {
                this.checked = false;
            } else {
                this.checked = true;
            }
        }
    }
}
