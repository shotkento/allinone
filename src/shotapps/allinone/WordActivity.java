package shotapps.allinone;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class WordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }

}
