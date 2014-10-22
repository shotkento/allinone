package shotapps.allinone.adapter;

import java.util.ArrayList;

import shotapps.allinone.R;
import shotapps.allinone.data.WordData;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class DialogListAdapter extends ArrayAdapter<WordData> {
    private static final String TAG = "DialogAdapter";
    Context context = null;
    ArrayList<WordData> list = null;
    private LayoutInflater mInflater = null;

    public DialogListAdapter(Activity activity,int layout, ArrayList<WordData> list) {
        super(activity, layout, list);
        this.context = activity;
        mInflater = activity.getLayoutInflater();
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_word, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        WordData wordData = list.get(position);
        holder.checkBox.setChecked(wordData.getChecked());
        holder.engWord.setText(wordData.getEngWord());
        holder.jpnWord.setText(wordData.getJpnWord());
        return convertView;
    }
    private static class ViewHolder {
        CheckBox checkBox;
        TextView engWord;
        TextView jpnWord;
        public ViewHolder(View view) {
            this.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            this.engWord = (TextView) view.findViewById(R.id.engListWord);
            this.jpnWord = (TextView) view.findViewById(R.id.jpnListWord);
        }
    }
}
