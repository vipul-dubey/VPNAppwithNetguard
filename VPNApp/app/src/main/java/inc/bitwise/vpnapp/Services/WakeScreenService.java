package inc.bitwise.vpnapp.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;
import inc.bitwise.vpnapp.Receivers.UserSwitchReceiver;

public class WakeScreenService extends Service {

    BroadcastReceiver mReceiver;
    UserSwitchReceiver receiver;
    MSASQLiteDB DBObj = null;

    public WakeScreenService( ){}

    @Override
    public void onCreate()
    {
        DBObj  = new MSASQLiteDB(getApplicationContext());
        try {
            Log.i("MSA", "Service on create");
            DBObj.PutLog("[WSS]Entered");
            // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
            /*IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            mReceiver = new ScreenReceiver(getApplicationContext(), true);
            registerReceiver(mReceiver, filter);*/


            DBObj.PutLog("[*] UserSwitch Receiver 1");
            receiver = new UserSwitchReceiver();
            IntentFilter ifilter = new IntentFilter();
            ifilter.addAction(Intent.ACTION_USER_BACKGROUND);
            ifilter.addAction(Intent.ACTION_USER_FOREGROUND);
            DBObj.PutLog("[*] Register UserSwitch Receiver 2");
            this.registerReceiver(receiver, ifilter);
            DBObj.PutLog("[*] END");


        }
        catch (Exception ex){
            DBObj.PutLog("[@WSS]EX1"+ex.toString());
        }
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        Log.i("MSA", "Service on start");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver!=null){unregisterReceiver(receiver);}
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}