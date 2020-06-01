package inc.bitwise.vpnapp.Receivers;


import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;
import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;
import inc.bitwise.vpnapp.Services.CheckPermissionAtInterval;

/**
 * Created by sakshit on 3/10/2016.
 */
public class CheckPermissionReceiver extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        MSASQLiteDB DBObj = new MSASQLiteDB(context);

        try {
            DBObj.PutLog("[CPR]start");
            CheckPermissionAtInterval permissionChk = new CheckPermissionAtInterval(context);
            permissionChk.execute();
        }
        catch (Exception e)
        {
            DBObj.PutLog("[@@CPR] EX1-" + e);
        }

    }

}