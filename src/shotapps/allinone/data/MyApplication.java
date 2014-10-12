package shotapps.allinone.data;

import java.util.ArrayList;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    private ArrayList<SentenceData> sentenceDataList;
    private ArrayList<WordData> wordDataList;
    private ArrayList<WordData> idiomDataList;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate");
    }

    public ArrayList<SentenceData> getSentenceDataList() {
        return sentenceDataList;
    }

    public ArrayList<WordData> getWordDataList() {
        Log.d(TAG, "getWordDataList");
        return wordDataList;
    }

    public ArrayList<WordData> getIdiomDataList() {
        return idiomDataList;
    }

    public void setSentenceDataList(ArrayList<SentenceData> sentenceDataList) {
        this.sentenceDataList = sentenceDataList;
    }

    public void setWordDataList(ArrayList<WordData> wordDataList) {
        Log.d(TAG, "setWordDataList wordDataList.size = " + wordDataList.size());
        this.wordDataList = wordDataList;
    }

    public void setIdiomDataList(ArrayList<WordData> idiomDataList) {
        this.idiomDataList = idiomDataList;
    }
}
