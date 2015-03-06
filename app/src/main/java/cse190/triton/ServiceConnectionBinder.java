package cse190.triton;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceConnectionBinder{
    private MusicService m_Serv=null;
    private ServiceConnection m_OnService;
    private boolean m_IsBound;
    private Activity m_Client;

    public ServiceConnectionBinder(Activity  i_Activity) {
        m_IsBound = false;
        this.m_Client = i_Activity;
        this.m_OnService = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder rawBinder) {
                m_Serv = ((MusicService.ServiceBinder) rawBinder).getService();
            }

            public void onServiceDisconnected(ComponentName className) {
                m_Serv = null;
            }
        };
        //doBindService();
    }

    public void doBindService() {
        if(!m_IsBound) {
            m_Client.bindService(new Intent(m_Client, MusicService.class), m_OnService, Context.BIND_AUTO_CREATE);
            m_IsBound = true;
        }
    }

    public void doUnbindService() {
        if (m_IsBound) {
            // Detach our existing connection.
            m_Client.unbindService(m_OnService);
            m_IsBound = false;
        }
    }

    public MusicService getConnectionService() {
        return m_Serv;
    }

}