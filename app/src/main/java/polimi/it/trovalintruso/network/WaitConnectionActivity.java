package polimi.it.trovalintruso.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;

import polimi.it.trovalintruso.R;

/**
 * Created by poool on 02/03/15.
 */
public class WaitConnectionActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProgressDialog dialog = ProgressDialog.show(WaitConnectionActivity.this,
                getString(R.string.wait),
                getString(R.string.inviting_player), true);
        dialog.show();
    }
}
