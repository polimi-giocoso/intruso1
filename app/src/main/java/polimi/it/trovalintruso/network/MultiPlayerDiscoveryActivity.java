package polimi.it.trovalintruso.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

import polimi.it.trovalintruso.App;
import polimi.it.trovalintruso.R;
import polimi.it.trovalintruso.model.Device;

/**
 * Created by Paolo on 01/03/2015.
 */
public class MultiPlayerDiscoveryActivity extends Activity {

    @InjectView(R.id.devices_discovery_listView)
    ListView devicesListView;

    private ProgressDialog dialog;
    Context context;

    @OnItemClick(R.id.devices_discovery_listView)
    void invitePlayer(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Device device = (Device) arg0.getItemAtPosition(arg2);
        App.gameHelper.connectToClient(device.getService().getHost(), device.getService().getPort());

    }
    private ArrayAdapter<Device> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_discovery);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initializeUi();
        context = this;
    }

    @Override
    protected void onPause() {
        App.gameHelper.removeServiceDiscoveryHandler();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.gameHelper.onActivityResume(this);
        Handler updateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mArrayAdapter.notifyDataSetChanged();
            }
        };
        App.gameHelper.setServiceDiscoveryHandler(updateHandler);
    }

    private void initializeUi() {
        ButterKnife.inject(this);
        mArrayAdapter = new ArrayAdapter<Device>(this,
                android.R.layout.simple_list_item_1,
                App.gameHelper.getDeviceList());
        devicesListView.setAdapter(mArrayAdapter);
    }

}
