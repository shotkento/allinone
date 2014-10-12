package shotapps.allinone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SentenceActivity extends BaseActivity {
    private final String TAG = "TrainingActivity";

    private Button mAnswerBtn;
    private Button mPlayBtn;
    private Button mWordBrn;
    private ArrayList<SentenceData> mSentenceDataList;
    private ArrayList<WordData> mWordDataList;
    private ArrayList<WordData> mCurrentWordDataList;
    private ArrayList<WordData> mIdiomDataList;
    private ArrayList<WordData> mCurrentIdiomDataList;
    private SentenceData mSentenceData;
    private WordData mWordData;
    private WordData mIdiomData;
    private TextView mNumber;
    private TextView mDay;
    private TextView mJapanese;
    private TextView mEnglish;
    private EditText mEdit;
    private Button mCorrectBtn;
    private Button mIncorrectBtn;
    private int mDataNumber;
    private boolean isNormal;
    private MediaPlayer mPlayer;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Start onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);

        mNumber = (TextView) findViewById(R.id.number);
        mDay = (TextView) findViewById(R.id.day);
        mJapanese = (TextView) findViewById(R.id.japanese_txt);
        mEnglish = (TextView) findViewById(R.id.english_txt);
        mWordBrn = (Button) findViewById(R.id.wordDialog_button);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        // データ取得
        mSentenceDataList = myApplication.getSentenceDataList();
        mSentenceData = mSentenceDataList.get(0);

        // 対象No.の単語リストを取得
        mWordDataList = myApplication.getWordDataList();
        mCurrentWordDataList = getCurrentWordList(mWordDataList);

        for (int i = 0; i < mCurrentWordDataList.size(); i++) {
            Log.d(TAG, "word = " + mCurrentWordDataList.get(i).getEngWord());
        }

        // 対象No.の熟語リストを取得
        mIdiomDataList = myApplication.getIdiomDataList();
        mCurrentIdiomDataList = getCurrentWordList(mIdiomDataList);

        for (int i = 0; i < mCurrentIdiomDataList.size(); i++) {
            Log.d(TAG, "word = " + mCurrentIdiomDataList.get(i).getEngWord());
        }

        mDataNumber = 1;
        isNormal = true;

        setData();

        // 日本語
        mJapanese.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNormal) {
                    mJapanese.setText(mSentenceData.jpn_sent_order);
                    isNormal = false;
                } else {
                    mJapanese.setText(mSentenceData.jpn_sent_normal);
                    isNormal = true;
                }
            }
        });

        // 回答入力エリア
        mEdit = (EditText) findViewById(R.id.editText);

        // 再生ボタン
        mPlayBtn = (Button) findViewById(R.id.play_button);
        mPlayBtn.setRotation(90);
        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
            }
        });

        // Answerボタン設定
        mAnswerBtn = (Button) findViewById(R.id.answer_button);
        mAnswerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                mEnglish.setText(mSentenceData.eng_sent);
                mAnswerBtn.setEnabled(false);
                mCorrectBtn.setVisibility(View.VISIBLE);
                mIncorrectBtn.setVisibility(View.VISIBLE);
            }
        });

        // WORDボタン
        mWordBrn.setOnClickListener(new OnClickListener() {
            final String[] test = { "a", "b", "c" };
            final boolean[] checked = { true, false, true };

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        SentenceActivity.this);
                builder.setMultiChoiceItems(test, checked,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which, boolean isChecked) {
                                checked[which] = isChecked;
                            }
                        })
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        // TODO DBに登録
                                    }
                                }).show();
            }
        });

        // Correctボタン設定
        mCorrectBtn = (Button) findViewById(R.id.correct_button);
        mCorrectBtn.setVisibility(View.INVISIBLE);
        mCorrectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setNext();
            }
        });

        // Incorrectボタン設定
        mIncorrectBtn = (Button) findViewById(R.id.incorrect_button);
        mIncorrectBtn.setVisibility(View.INVISIBLE);
        mIncorrectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setNext();
            }
        });

    }

    private ArrayList<WordData> getCurrentWordList(ArrayList<WordData> list) {
        ArrayList<WordData> currentWordList = new ArrayList<WordData>();
        for(int i = 0; i < list.size(); i++) {
            WordData data = list.get(i);
            if(data.getSentNum1() == mSentenceData.id || data.getSentNum2() == mSentenceData.id) {
                currentWordList.add(data);
            }
        }
        return currentWordList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer = MediaPlayer.create(
                this,
                getResources().getIdentifier("n" + mSentenceData.id, "raw",
                        getPackageName()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null) {
            stopSound();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void setData() {
        mNumber.setText("No." + mSentenceData.id);
        mJapanese.setText(mSentenceData.jpn_sent_normal);
        mEnglish.setText("");
    }

    private void setNext() {
        if (mSentenceDataList.size() > mDataNumber) {
            mSentenceData = mSentenceDataList.get(mDataNumber);
            mCurrentWordDataList = getCurrentWordList(mWordDataList);

            for (int i = 0; i < mCurrentWordDataList.size(); i++) {
                Log.d(TAG, "word = " + mCurrentWordDataList.get(i).getEngWord());
            }

            mCurrentIdiomDataList = getCurrentWordList(mIdiomDataList);

            for (int i = 0; i < mCurrentIdiomDataList.size(); i++) {
                Log.d(TAG, "word = " + mCurrentIdiomDataList.get(i).getEngWord());
            }

            setData();
            mDataNumber++;

            mCorrectBtn.setVisibility(View.INVISIBLE);
            mIncorrectBtn.setVisibility(View.INVISIBLE);
            mAnswerBtn.setEnabled(true);
            mEdit.setText("");
            mEdit.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(mEdit, 0);
            stopSound();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        byte[] buf = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(mSentenceData);
            buf = byteOut.toByteArray();
        } catch (Exception e) {
        }

        outState.putByteArray("test", buf);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO 自動生成されたメソッド・スタブ
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            byte[] buf = savedInstanceState.getByteArray("test");
            if (buf != null || buf.length == 0) {
                try {
                    ByteArrayInputStream byteInput = new ByteArrayInputStream(
                            buf);
                    ObjectInputStream objectInput = new ObjectInputStream(
                            byteInput);
                    mSentenceData = (SentenceData) objectInput.readObject();
                    setData();
                } catch (Exception e) {
                }
            }
        }
    }

    private void playSound() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mPlayBtn.setText(" ▲︎");
        } else {
            mPlayer = MediaPlayer.create(
                    getApplicationContext(),
                    getResources().getIdentifier("n" + mSentenceData.id, "raw",
                            getPackageName()));
            mPlayer.seekTo(1500);
            mPlayer.setLooping(true);
            mPlayer.start();
            mPlayBtn.setText("〓");
        }
    }

    private void stopSound() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mPlayBtn.setText(" ▲︎");
        }
    }

}
