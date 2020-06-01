package inc.bitwise.vpnapp.CommonUtils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import androidx.core.app.NotificationCompat;
import inc.bitwise.vpnapp.BuildConfig;
import inc.bitwise.vpnapp.R;
import inc.bitwise.vpnapp.Services.WakeScreenService;
import inc.bitwise.vpnapp.TapInProcess.TapIn;
import inc.bitwise.vpnazure.Models.UserRegistration;
import inc.bitwise.vpnazure.Services.RegistrationService;
import inc.bitwise.vpnazure.Services.Service;
import inc.bitwise.vpnazure.Services.TapInService;
import inc.bitwise.vpnazure.Utility.AsyncLoader;

public class CommonUtils {
    Context utilityContext;
    public   static String   android_id ;
    private   NotificationCompat.Builder mBuilder = null;
    private NotificationManager mNotificationManager= null;
    public static String UDID;
    MSASQLiteDB dbObj;
    DeviceAdminUtility adminUtility;

    public static boolean ViolationReported = false;
    public static boolean notifyuserSwitchViaEmail = false;
    public static boolean notifyuserSwitchViaSMS = false;

    public CommonUtils(Context context)
    {
        utilityContext = context;
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        dbObj = new MSASQLiteDB(context);
        adminUtility = new DeviceAdminUtility(context);
        initialise(context);
    }
    private void initialise(Context context){
        Resources resources = context.getResources();
        String azureHost = resources.getString(R.string.AzureHost);
        Service.initialize(context, azureHost, BuildConfig.DEBUG);
        //session = new UserSessionManager(getApplicationContext());
    }
    public static String getDeviceid()
    {
        return android_id;
    }

    public static boolean isRooted() throws Exception
    {
        String tags = Build.TAGS;
        if ((tags != null) && tags.contains("test-keys"))
            return true;
        try {
            String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                    "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
            for (String path : paths) {
                if (new File(path).exists()) return true;
            }
            return false;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            String[] commands = {
                    "/system/xbin/which su",
                    "/system/bin/which su",
                    "which su"
            };
            for (String command: commands) {
                try {
                    int exitValue = Runtime.getRuntime().exec(command).waitFor();
                    return exitValue == 0;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } else {
            // get from build info
            // try executing commands

            return canExecuteCommand("/system/xbin/which su")
                    || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");

        }

        return false;
    }
    // executes a command on the system
    private static boolean canExecuteCommand(String command) throws Exception {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }
        return executedSuccesfully;
    }

    // Below code is used in case of push notifications.
    public void sendNotification(String msg ) {
        try {
            NotificationManager mNotificationManager= (NotificationManager) utilityContext.getSystemService(utilityContext.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = null;
            // after sending notification which activity to open
            PendingIntent contentIntent = PendingIntent.getActivity(utilityContext , 0, new Intent(utilityContext, TapIn.class), 0);
            mBuilder.setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(utilityContext.getResources().getString(R.string.NotificationName))
                    .setContentText(msg)
                    .setOngoing(true);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(15, mBuilder.build());

        } catch (Exception e) {
            //dbObj.PutLog("[@@CUtil]EX4 " + e.toString());
        }
    }


    public void StopServicesAtOutAndIcon () throws Exception
    {
        ViolationReported = false;
        NotificationManager mNotificationManager = (NotificationManager) utilityContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        int alarm_code = 1234567;

        Intent intent = new Intent(utilityContext,WakeScreenService.class);
        utilityContext.stopService(intent);
        Intent intentstoppinging = new Intent(utilityContext, WakeScreenService.class);
        PendingIntent senderstoppinging = PendingIntent.getBroadcast(utilityContext, alarm_code, intentstoppinging, 0);
        AlarmManager alarmManagerstop = (AlarmManager)utilityContext.getSystemService(utilityContext.ALARM_SERVICE);
        alarmManagerstop.cancel(senderstoppinging);

    }

    public void SendNotification(UserRegistration userRegistration,String emailSubject,String adminMobNo){
        Boolean CheckInStatus = adminUtility.GetCameraStatus();

        if(notifyuserSwitchViaSMS){

            if(SendSMSNotification(utilityContext, userRegistration.getUserName(), userRegistration.departmentName, userRegistration.mobileNo,adminMobNo)) {
                dbObj.PutLog("[u]set notifyuserSwitchViaSMS - false");
                notifyuserSwitchViaSMS = false;
            }
            else {
                notifyuserSwitchViaSMS = true;
                dbObj.PutLog("[u]set notifyuserSwitchViaSMS - true");
            }
        }
        if(notifyuserSwitchViaEmail){
            try {

                AsyncLoader.load(new AsyncLoader.SyncBlock()
                {
                    @Override
                    public void run() throws Exception
                    {

                        Boolean isNotified = RegistrationService.sharedInstance().appNotification(userRegistration,emailSubject);
                        if(isNotified){
                            AsyncLoader.loadOnUIThread(new AsyncLoader.CompletionBlock()
                            {
                                @Override
                                public void run(Exception exception)
                                {
                                    Toast.makeText(utilityContext, "Email Sent Successful", Toast.LENGTH_SHORT).show();
                                    notifyuserSwitchViaEmail = false;
                                }
                            });
                        }
                        else notifyuserSwitchViaEmail = true;
                    }
                }, new AsyncLoader.CompletionBlock() {
                    @Override
                    public void run(Exception exception) {
                        //LoadingDialog.dismiss();
                        Toast.makeText(utilityContext, "[UPS]Ntwk Err "+exception.toString(), Toast.LENGTH_SHORT).show();
                        notifyuserSwitchViaEmail = true;
                    }
                });
            } catch (Exception e) {
                Log.i("MSA","isregisteredException "+e.toString());
                e.printStackTrace();
            }

            if(notifyuserSwitchViaSMS && notifyuserSwitchViaEmail)
            {
                ViolationReported = true;

                //    int alarm_code = 1234567;
                //  StopServicesAtOutAndIcon(alarm_code);
                //dbObj.PutLog("[u] Stop");
            }
        }


    }

    public void TapIntrackingUpdate(String username, String tapValue){
        try{
            Toast.makeText(utilityContext, "TapIntrackingUpdate", Toast.LENGTH_SHORT).show();
            final inc.bitwise.vpnazure.Models.TapInOut userTapInTracking = new inc.bitwise.vpnazure.Models.TapInOut();
            userTapInTracking.setUserName(username);
            userTapInTracking.setTapValue(tapValue);


            //LoadingDialog.show(utilityContext, getString(R.string.InsertingTracking));
            try {

                AsyncLoader.load(new AsyncLoader.SyncBlock() {
                    @Override
                    public void run() throws Exception {

                        JSONObject isTapValueRegistered = TapInService.sharedInstance().tapInOutUser(userTapInTracking);
                        String isSuccess = isTapValueRegistered.getString("isSuccess");

                        AsyncLoader.loadOnUIThread(new AsyncLoader.CompletionBlock() {
                            @Override
                            public void run(Exception exception) {
                                if (isSuccess.equalsIgnoreCase("true")) {
                                    //LoadingDialog.dismiss();
                                    //session.createUserLoginSession(userRegistration);
                                    Toast.makeText(utilityContext, "TapValue Registered Successful", Toast.LENGTH_SHORT).show();
                                    //TransitionManager.startActivity(UserRegistration.this, TapIn.class);
                                    //finish();
                                }
                                else{
                                    //LoadingDialog.dismiss();
                                    try {
                                        Toast.makeText(utilityContext, isTapValueRegistered.getJSONArray("errorMessage").getJSONObject(0).getString("value").toString(), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    }
                }, new AsyncLoader.CompletionBlock() {
                    @Override
                    public void run(Exception exception) {
                        //LoadingDialog.dismiss();
                        Toast.makeText(utilityContext, "Network Error " + exception.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Log.i("MSA", "isregisteredException " + e.toString());
                Toast.makeText(utilityContext, "TapIntrackingUpdate " + e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


        }
        catch (Exception e)
        {
            Log.i("URException ","@@@@###### "+e.toString());
        }

    }

    public boolean SendSMSNotification(Context context, String UserName, String CompanyName, String MobileNo, String adminMobNo) {

        try {
            String SMSAndPushData = context.getResources().getString(R.string.UserSwitchNotification);
            SMSAndPushData = SMSAndPushData.replace("[UserName]", UserName);
            SMSAndPushData = SMSAndPushData.replace("[Department]", CompanyName);
            SMSAndPushData = SMSAndPushData.replace("[Mobile]", MobileNo);

            if (new SecuritySMS(context).sendSms(adminMobNo, SMSAndPushData, false)) {
                Toast.makeText(context, "SMS sent.", Toast.LENGTH_SHORT).show();
                return true;
            } else return false;
        }
        catch (Exception e){
            dbObj.PutLog("[@@mDPR]SMS "+e.toString());
            return false;
        }
    }
}
