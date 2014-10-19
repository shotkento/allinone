package shotapps.allinone;

import java.util.ArrayList;

import shotapps.allinone.data.WordData;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class WordActivity extends BaseActivity {
    private ArrayList<WordData> mWordDataList;
    private ArrayList<WordData> mWordStockList;
    private WordData mWordData;

    private TextView mEngTestWord;
    private TextView mJpnTestWord;
    private TextView mRoundText;
    private Button mCorrectBtn;
    private Button mIncorrectBtn;

    private int mCountList;
    private int mRound;
    private Handler mHandler;
    private Runnable mSetJpnWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        mEngTestWord = (TextView) findViewById(R.id.engTestWord);
        mJpnTestWord = (TextView) findViewById(R.id.jpnTestWord);
        mRoundText = (TextView) findViewById(R.id.roundTxt);
        mCorrectBtn = (Button) findViewById(R.id.wordCorrcet);
        mIncorrectBtn = (Button) findViewById(R.id.wordIncorrect);

        // 単語リストを取得
        mWordDataList = myApplication.getWordDataList();
        mWordStockList = new ArrayList<WordData>();

        mHandler = new Handler();

        mCountList = 0;
        mRound = 1;
        setWord();

        // わかるボタン
        mCorrectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // DBアップデート

                // 次のワードをセット
                if (!setWord()) {
                    // データなしなので次のリストへ
                    goNextRound();
                }
            }
        });

        // わかんないボタン
        mIncorrectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ContentValues cv = new ContentValues();
//                cv.put("checked", boolean2Int(checked));
//
//                db.update(table, cv, "_id = " + id, null);
                // わからなかったワードをストックしておく
                mWordStockList.add(mWordData);

                // 次のワードをセット
                if (!setWord()) {
                    // データなしなので次のリストへ
                    goNextRound();
                }
            }
        });
    }

    private void goNextRound() {
        // ストックしたワードを入れる
        mWordDataList = mWordStockList;
        mWordStockList = null;
        mWordStockList = new ArrayList<WordData>();
        mCountList = 0;
        if (!setWord()) {
            // もうデータがないのでダイアログだしておわり
           AlertDialog.Builder dialog = new AlertDialog.Builder(WordActivity.this);
           dialog.setCancelable(false);
           dialog.setTitle("お疲れ様でした！");
           dialog.setNegativeButton("トップへ戻る", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
           dialog.show();
        } else {
            mRound++;
            mRoundText.setText("Round " + mRound);
        }
    }

    private boolean setWord() {
        mHandler.removeCallbacks(mSetJpnWord);
        if (mWordDataList.size() <= mCountList) {
            return false;
        }
        mWordData = mWordDataList.get(mCountList);
        mEngTestWord.setText(mWordData.getEngWord());
        mJpnTestWord.setText("");
        mSetJpnWord = new SetJpnWordHandler();
        mHandler.postDelayed(mSetJpnWord, 1000);
        mCountList++;
        return true;
    }

    class SetJpnWordHandler implements Runnable {
        @Override
        public void run() {
            mJpnTestWord.setText(mWordData.getJpnWord());
        }
    }
}
