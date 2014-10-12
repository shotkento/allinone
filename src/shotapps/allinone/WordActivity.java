package shotapps.allinone;

import java.util.ArrayList;

import shotapps.allinone.data.WordData;
import android.app.ActionBar;
import android.content.res.Resources;
import android.os.Bundle;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations.Orientation;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;

public class WordActivity extends BaseActivity {
    private CardContainer mCardContainer;
    private ArrayList<WordData> mWordDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        Resources r = getResources();

        mWordDataList = myApplication.getWordDataList();

        mCardContainer = (CardContainer) findViewById(R.id.cardContainer);
        mCardContainer.setOrientation(Orientation.Ordered);

        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);
        for (int i = 0; i < mWordDataList.size(); i++) {
            CardModel card = new CardModel(mWordDataList.get(i).getEngWord(),
                    mWordDataList.get(i).getJpnWord());
            adapter.add(card);
        }
        mCardContainer.setAdapter(adapter);
    }
}
