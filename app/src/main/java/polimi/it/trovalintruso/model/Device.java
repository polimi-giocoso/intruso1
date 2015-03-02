package polimi.it.trovalintruso.model;

import android.net.nsd.NsdServiceInfo;

import java.net.InetAddress;

/**
 * Created by poool on 02/03/15.
 */
public class Device {

    private InetAddress _host;
    private int _port;
    private String _name;
    private NsdServiceInfo _service;

    public Device(InetAddress host, String name, int port) {
        _host = host;
        _name = name;
        _port = port;
    }

    public Device(NsdServiceInfo info, String name) {
        _service = info;
    }

    public NsdServiceInfo getService() {
        return _service;
    }

    public int getPort() {
        return _port;
    }
    public String getName() {
        return _name;
    }
    public InetAddress getHost() {
        return _host;
    }

    @Override
    public String toString() {
        //return _name + " host: " + _host.toString() + " port: " + _port;
        return _service.getServiceName() + " " + _service.getHost() + " " +_service.getPort();
    }

    @Override
    public boolean equals(Object o) {
        return _service.getServiceName().equals(((Device)o).getService().getServiceName());
    }
}
