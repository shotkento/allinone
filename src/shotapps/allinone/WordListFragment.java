package shotapps.allinone;

import java.util.ArrayList;
import java.util.List;

import shotapps.allinone.BaseActivity.WordData;
import shotapps.allinone.data.MyApplication;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMyApplication = (MyApplication) getActivity().getApplication();

        ArrayList<WordData> dataList = null;
        switch (getArguments().getInt("num")) {
            case 0:
                dataList = mMyApplication.getWordDataList();
                break;
            case 1:
                dataList = mMyApplication.getIdiomDataList();
                break;
        }
        Log.d(TAG, "wordDataList.size = " + dataList.size());

         // Adapterを生成してセット
         setListAdapter(new CustomAdapter(getActivity(), dataList));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    public class CustomAdapter extends ArrayAdapter<WordData> {
        LayoutInflater mInflater;

        public CustomAdapter(Context context, List<WordData> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_word, null);
            }
            // 写真とテキストをセットする
            final WordData word = (WordData) this.getItem(position);
            if (word != null) {
                CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                checkBox.setChecked(word.getChecked());

                TextView engWord = (TextView) convertView.findViewById(R.id.engListWord);
                engWord.setText(word.getEngWord());

                TextView jpnWord = (TextView) convertView.findViewById(R.id.jpnListWord);
                jpnWord.setText(word.getJpnWord());
            }
            return convertView;
        }
    }
}
