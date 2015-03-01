package polimi.it.trovalintruso.multiplayer;

import android.bluetooth.BluetoothAdapter;

import polimi.it.trovalintruso.multiplayer.MultiPlayerHelper.ConnectionType;

/**
 * Created by Paolo on 01/03/2015.
 */
public class MultiPlayerConnection {

    private ConnectionType _type;

    public void init(ConnectionType type) {
        _type = type;
        if(_type == ConnectionType.Bluetooth) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            }
        }
    }

    public void connect(){

    }
}
