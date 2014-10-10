package shotapps.allinone;

import java.util.ArrayList;

import shotapps.allinone.MainActivity.WordData;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class WordListActivity extends Activity {
    private static final String TAG = WordListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        ArrayList<WordData> dataList = findWordData();

        if (dataList.size() == 0) {
            Log.e(TAG, "WordData is not found!!");
            return;
        }
        Log.d(TAG, "mDatalist size is " + dataList.size());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.word_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
