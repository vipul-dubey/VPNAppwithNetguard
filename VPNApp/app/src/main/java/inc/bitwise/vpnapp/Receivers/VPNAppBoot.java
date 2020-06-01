package inc.bitwise.vpnapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import inc.bitwise.vpnapp.CommonUtils.DeviceAdminUtility;
import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;

public class VPNAppBoot extends BroadcastReceiver {

    MSASQLiteDB DBObj;
    Context _appContext;
    DeviceAdminUtility deviceAdminUtility;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            _appContext = context;
            DBObj = new MSASQLiteDB(_appContext);
            deviceAdminUtility = new DeviceAdminUtility(context);
            DBObj.PutLog("[RB]Reboot");

            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) && deviceAdminUtility.GetCameraStatus())
            {
                //Start WakeScreenService to catch Screen ON-OFF broadcast intent
                /*Intent myIntent = new Intent(context, WakeScreenService.class);
                context.startService(myIntent);*/
            }
        }
        catch (Exception e) {
            DBObj.PutLog("[RB]EX1 "+e.toString());
        }
    }
}
