package shotapps.allinone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import shotapps.allinone.MainActivity.Data;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TrainingActivity extends Activity {
    private Button mAnswerBtn;
    private Button mPlayBtn;
    private ArrayList<Data> mDataList;
    private Data mData;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        mNumber = (TextView)findViewById(R.id.number);
        mDay = (TextView)findViewById(R.id.day);
        mJapanese = (TextView)findViewById(R.id.japanese_txt);
        mEnglish = (TextView)findViewById(R.id.english_txt);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        // intentからデータ取得
        mDataList = (ArrayList<Data>) getIntent().getSerializableExtra("data");
        mData = mDataList.get(0);
        mDataNumber = 1;
        isNormal = true;

        setData();

        // 日本語
        mJapanese.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isNormal) {
                    mJapanese.setText(mData.japanese_txt_order);
                    isNormal = false;
                } else {
                    mJapanese.setText(mData.japanese_txt_normal);
                    isNormal = true;
                }
            }
        });

        // 回答入力エリア
        mEdit = (EditText)findViewById(R.id.editText);

        // Answerボタン設定
        mAnswerBtn = (Button)findViewById(R.id.answer_button);
        mAnswerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                mEnglish.setText(mData.english_txt);
                mAnswerBtn.setEnabled(false);
                mCorrectBtn.setVisibility(View.VISIBLE);
                mIncorrectBtn.setVisibility(View.VISIBLE);
            }
        });

        mPlayBtn = (Button)findViewById(R.id.play_button);
        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
            }
        });

        // Correctボタン設定
        mCorrectBtn = (Button)findViewById(R.id.correct_button);
        mCorrectBtn.setVisibility(View.INVISIBLE);
        mCorrectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setNext();
            }
        });

        // Incorrectボタン設定
        mIncorrectBtn = (Button)findViewById(R.id.incorrect_button);
        mIncorrectBtn.setVisibility(View.INVISIBLE);
        mIncorrectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setNext();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer = MediaPlayer.create(this, getResources().getIdentifier("n"+mData.id, "raw", getPackageName()));
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

    private void setData(){
        mNumber.setText("No." + mData.id);
        mDay.setText("Day" + mData.day);
        mJapanese.setText(mData.japanese_txt_normal);
        mEnglish.setText("");
    }

    private void setNext() {
        if(mDataList.size() > mDataNumber) {
            mData = mDataList.get(mDataNumber);
            setData();
            mDataNumber++;

            mCorrectBtn.setVisibility(View.INVISIBLE);
            mIncorrectBtn.setVisibility(View.INVISIBLE);
            mAnswerBtn.setEnabled(true);
            mEdit.setText("");
            mEdit.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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
            objectOut.writeObject(mData);
            buf = byteOut.toByteArray();
        } catch (Exception e) {
        }

        outState.putByteArray("test", buf);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO 自動生成されたメソッド・スタブ
        super.onRestoreInstanceState(savedInstanceState);
        if ( savedInstanceState != null ) {
            byte[] buf = savedInstanceState.getByteArray("test");
            if (buf != null || buf.length == 0) {
                try {
                    ByteArrayInputStream byteInput = new ByteArrayInputStream(buf);
                    ObjectInputStream objectInput = new ObjectInputStream(byteInput);
                    mData = (Data) objectInput.readObject();
                    setData();
                } catch (Exception e) {
                }
            }
        }
    }

    private void playSound() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mPlayBtn.setText(" ▶︎");
            mPlayBtn.setRotation(0);
        } else {
            mPlayer = MediaPlayer.create(getApplicationContext(), getResources().getIdentifier("n"+mData.id, "raw", getPackageName()));
            mPlayer.seekTo(1500);
            mPlayer.setLooping(true);
            mPlayer.start();
            mPlayBtn.setText("〓");
            mPlayBtn.setRotation(90);
        }
    }

    private void stopSound() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mPlayBtn.setText(" ▶︎");
            mPlayBtn.setRotation(0);
        }
    }

}
