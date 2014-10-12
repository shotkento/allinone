package shotapps.allinone;

import java.util.ArrayList;

import shotapps.allinone.data.SentenceData;
import shotapps.allinone.data.WordData;
import android.app.ActionBar;
import android.content.Intent;
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

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private final int DAY_MIN = 1;
    private final int DAY_MAX = 60;

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

        setSettingButtons();

        setStartButtons();
    }

    private void setSettingButtons() {
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
    }

    private void setStartButtons() {
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

                ArrayList<SentenceData> sentenceDataList = findData(dayStart,
                        dayEnd, checkRandom(random));
                ArrayList<WordData> wordDataList = findWordData(WORD_TABLE,
                        dayStart, dayEnd, ORDER_ASC);
                ArrayList<WordData> idiomDataList = findWordData(IDIOM_TABLE,
                        dayStart, dayEnd, ORDER_ASC);
                if (sentenceDataList.size() == 0) {
                    Log.e(TAG, "SentenceData is not found!!");
                    return;
                }

                myApplication.setSentenceDataList(sentenceDataList);
                myApplication.setWordDataList(wordDataList);
                myApplication.setIdiomDataList(idiomDataList);

                Intent intent = new Intent(MainActivity.this,
                        SentenceActivity.class);
                startActivity(intent);
                Log.d(TAG, "Start SentenceActivity");
            }
        });

        // Wordボタン設定
        Button wordBtn = (Button) findViewById(R.id.wordTestBtn);
        wordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        WordActivity.class);
                startActivity(intent);
            }
        });

        // WordListボタン
        Button wordListBtn = (Button) findViewById(R.id.wordListBtn);
        wordListBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,
                        WordListActivity.class);
                startActivity(intent);
                Log.d(TAG, "Start WordListActivity");
            }
        });
    }
}
