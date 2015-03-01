package polimi.it.trovalintruso.multiplayer;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import java.util.ArrayList;

import polimi.it.trovalintruso.network.BluetoothHelper;

/**
 * Created by Paolo on 01/03/2015.
 */
public class MultiPlayerHelper {

    public static enum ConnectionType {
        Bluetooth,
        WifiP2P
    }

    public static ConnectionType[] availableConnections() {
        ArrayList<ConnectionType> list = new ArrayList<>();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            list.add(ConnectionType.Bluetooth);
        }
        return (ConnectionType[]) list.toArray();
    }

    public static void init(Context context) {
        for(ConnectionType t : availableConnections()) {
            if(t == ConnectionType.Bluetooth) {
                BluetoothHelper.enableDiscoverability(context);
            }
        }
    }
}
