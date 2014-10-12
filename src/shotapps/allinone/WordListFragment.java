package shotapps.allinone;

import java.util.ArrayList;
import java.util.List;

import shotapps.allinone.data.MyApplication;
import shotapps.allinone.data.WordData;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class WordListFragment extends ListFragment {
    private static final String TAG = WordListFragment.class.getSimpleName();
    private MyApplication mMyApplication;
    private WordListFragmentListener mListener;
    private String tableName;

    public interface WordListFragmentListener {
        public void saveChecked(String table, int id, boolean checked);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (WordListFragmentListener) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMyApplication = (MyApplication) getActivity().getApplication();

        ArrayList<WordData> dataList = null;
        switch (getArguments().getInt("num")) {
        case 0:
            dataList = mMyApplication.getWordDataList();
            tableName = BaseActivity.WORD_TABLE;
            break;
        case 1:
            dataList = mMyApplication.getIdiomDataList();
            tableName = BaseActivity.IDIOM_TABLE;
            break;
        }
        Log.d(TAG, "wordDataList.size = " + dataList.size());

        // Adapterを生成してセット
        setListAdapter(new CustomAdapter(getActivity(), dataList));
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position,
            long id) {
        super.onListItemClick(listView, view, position, id);

        // 選択された１行のデータを取得
        WordData data = (WordData) listView.getItemAtPosition(position);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }

        mListener.saveChecked(tableName, data.getId(), checkBox.isChecked());
    }

    public class CustomAdapter extends ArrayAdapter<WordData> {
        LayoutInflater mInflater;

        public CustomAdapter(Context context, List<WordData> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_word, null);
            }
            // 写真とテキストをセットする
            final WordData word = (WordData) this.getItem(position);
            if (word != null) {
                CheckBox checkBox = (CheckBox) convertView
                        .findViewById(R.id.checkBox);
                checkBox.setChecked(word.getChecked());

                TextView engWord = (TextView) convertView
                        .findViewById(R.id.engListWord);
                engWord.setText(word.getEngWord());

                TextView jpnWord = (TextView) convertView
                        .findViewById(R.id.jpnListWord);
                jpnWord.setText(word.getJpnWord());
            }
            return convertView;
        }
    }
}
