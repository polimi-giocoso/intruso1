package polimi.it.trovalintruso.network;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Paolo on 01/03/2015.
 */
public class BluetoothHelper {

    public static void enableDiscoverability(Context context) {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        context.startActivity(discoverableIntent);
    }
}
