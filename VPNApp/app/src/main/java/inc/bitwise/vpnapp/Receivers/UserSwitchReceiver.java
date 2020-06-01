package inc.bitwise.vpnapp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.HashMap;

import inc.bitwise.vpnapp.CommonUtils.CommonUtils;
import inc.bitwise.vpnapp.CommonUtils.DeviceAdminUtility;
import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;
import inc.bitwise.vpnapp.CommonUtils.Model.UserSessionManager;
import inc.bitwise.vpnazure.Models.UserRegistration;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UserSwitchReceiver extends BroadcastReceiver {

    private static final String TAG = "UserSwitchReceiver";
    public MSASQLiteDB dbObj = null;
    CommonUtils commonUtils;
    DeviceAdminUtility deviceAdminUtility;
    UserSessionManager session;
    String emailSubject;
    UserRegistration userRegistration;
    String adminMobNo;

    @Override
    public void onReceive(Context context, Intent intent) {
        commonUtils = new CommonUtils(context);
        deviceAdminUtility = new DeviceAdminUtility(context);
        if (context != null) {
            try {
                dbObj = new MSASQLiteDB(context);
                session = new UserSessionManager(getApplicationContext());

                HashMap<String,String> stringHashMap = session.getUserDetails();
                userRegistration = new UserRegistration();
                userRegistration.setUserName(stringHashMap.get("userName"));
                userRegistration.setMobileNo(stringHashMap.get("mobileNo"));
                userRegistration.setDepartmentName(stringHashMap.get("departmentName"));
                emailSubject = "VPN App UserProfileSwitch Alert Notification";
                adminMobNo = stringHashMap.get("adminMobNo");

                boolean userSentBackground = intent.getAction().equals(Intent.ACTION_USER_BACKGROUND);
                boolean userSentForeground = intent.getAction().equals(Intent.ACTION_USER_FOREGROUND);
                if (userSentBackground && deviceAdminUtility.GetCameraStatus() && !commonUtils.ViolationReported){
                    commonUtils.notifyuserSwitchViaEmail = true;
                    commonUtils.notifyuserSwitchViaSMS = true;
                    commonUtils.SendNotification(userRegistration,emailSubject,adminMobNo);
                }

                if (userSentForeground) {
                    commonUtils.ViolationReported = false;
                    //mMediaPlayer.stop();
                }

            } catch (Exception ex) {
                Toast.makeText(context, "@@USR " + ex.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}