package polimi.it.trovalintruso.multiplayer;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import polimi.it.trovalintruso.App;
import polimi.it.trovalintruso.model.Device;

/**
 * Created by Paolo on 01/03/2015.
 */
public class MultiPlayerServiceHelper {

    Context mContext;

    NsdManager mNsdManager;
    //NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;

    public static final String SERVICE_TYPE = "_http._tcp.";

    public static final String TAG = "NsdHelper";
    private final String SERVICE_NAME = "NsdTrovaIntruso1";
    public String mServiceName = "NsdTrovaIntruso1";

    NsdServiceInfo mService;

    private ArrayList<Device> mDeviceList;
    private Handler mUpdateHandler;

    public ArrayList<Device> getDevices() {
        return mDeviceList;
    }

    public MultiPlayerServiceHelper(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mDeviceList = new ArrayList<>();
    }

    public void setUpdateHandler(Handler updateHandler) {
        mUpdateHandler = updateHandler;
    }

    public void removeUpdateHandler() {
        mUpdateHandler = null;
    }

    public void initializeNsd() {
        mServiceName = "";
        //initializeResolveListener();
        initializeDiscoveryListener();
        initializeRegistrationListener();

        //mNsdManager.init(mContext.getMainLooper(), this);

    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same machine: " + mServiceName);
                } else if (service.getServiceName().contains(SERVICE_NAME)){
                    mNsdManager.resolveService(service, new MyResolveListener());
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                Device tmp = null;
                for(Device d : mDeviceList) {
                    if(d.getService().getServiceName().equals(service.getServiceName()))
                        tmp = d;
                }
                if(tmp != null)
                    mDeviceList.remove(tmp);
                if(mUpdateHandler != null)
                    mUpdateHandler.sendMessage(new Message());
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    private class MyResolveListener implements NsdManager.ResolveListener {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.e(TAG, "Resolve failed" + errorCode);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

            if (serviceInfo.getServiceName().equals(mServiceName)) {
                Log.d(TAG, "Same IP.");
                return;
            }
            mService = serviceInfo;
            String[] names = serviceInfo.getServiceName().split("@");
            if(names.length > 1) {
                Device dev = new Device(serviceInfo, names[1]);
                Boolean found = false;
                for (Device d : mDeviceList) {
                    if (d.equals(dev))
                        found = true;
                }
                if (!found)
                    mDeviceList.add(dev);
                if (mUpdateHandler != null)
                    mUpdateHandler.sendMessage(new Message());
            }
        }
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                mServiceName = NsdServiceInfo.getServiceName();
                Log.d(TAG, "Service registered: " + mServiceName);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }

        };
    }

    public void registerService(int port) {
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(SERVICE_NAME + "@" + App.gameHelper.getDeviceName() + "@");
        serviceInfo.setServiceType(SERVICE_TYPE);
        //serviceInfo.setAttribute("deviceName", mDeviceName);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

    }

    public void discoverServices() {
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }

    public void tearDown() {
        mNsdManager.unregisterService(mRegistrationListener);
        mDeviceList.clear();
    }
}
