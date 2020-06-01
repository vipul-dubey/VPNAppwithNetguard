package inc.bitwise.vpnapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;
import inc.bitwise.vpnapp.CommonUtils.Model.UserSessionManager;

public class ScreenReceiver extends BroadcastReceiver {

    public static boolean wasScreenOn;
    public MSASQLiteDB DBObj;
    protected static final int alarm_code = 1234567;
    UserSessionManager session;
    public ScreenReceiver(Context ctx, Boolean screenStatus )
    {
        wasScreenOn = screenStatus;
        session = new UserSessionManager(ctx);
        //  StartPingingIntent(ctx);

        // TODO Auto-generated constructor stub
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        DBObj = new MSASQLiteDB(context);
        try
        {
            DBObj.PutLog("[ScrnRcvr]wasScreenOn - "+wasScreenOn);

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
                //wasScreenOn = false;
                //session.setScreenStatus();
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {
                wasScreenOn = true;
                DBObj.PutLog("[SR]set LastScreenStatus");
            }
        }
        catch(Exception e)
        {
            DBObj.PutLog("[@@SR]EX1- "+e.toString());
        }
    }
}