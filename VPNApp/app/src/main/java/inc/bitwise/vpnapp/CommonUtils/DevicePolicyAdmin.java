package inc.bitwise.vpnapp.CommonUtils;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.HashMap;

import inc.bitwise.vpnapp.BuildConfig;
import inc.bitwise.vpnapp.CommonUtils.LoadingDialog.LoadingDialog;
import inc.bitwise.vpnapp.CommonUtils.Model.UserSessionManager;
import inc.bitwise.vpnapp.R;
import inc.bitwise.vpnazure.Models.UserRegistration;
import inc.bitwise.vpnazure.Services.RegistrationService;
import inc.bitwise.vpnazure.Services.Service;
import inc.bitwise.vpnazure.Utility.AsyncLoader;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DevicePolicyAdmin extends DeviceAdminReceiver {

    UserSessionManager session;
    String adminMobNo;

    public DevicePolicyAdmin(){}

    MSASQLiteDB dbObj = null;

    private void initialise(Context context){
        Resources resources = context.getResources();
        String azureHost = resources.getString(R.string.AzureHost);
        Service.initialize(context, azureHost, BuildConfig.DEBUG);
        session = new UserSessionManager(getApplicationContext());
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        DeviceAdminUtility utilObj = new DeviceAdminUtility(context);
        utilObj.setPasswordExpirationTimeout();
        Toast.makeText(context, "Device password is now changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent)
    {
        initialise(context);

        dbObj = new MSASQLiteDB(context);
        //session = new UserSessionManager(getApplicationContext());
        //TODO: need to check why this ApplicationSyayus is used
        //Till now we do not find any use of Table ApplicationStatus
        //  dbObj.AddApplicationStatusInfo(0);
        //ArrayList<String> data = dbObj.getUserTAPInfo();
        //dbObj.PutLog("[mDPR]onDisabled getAdminInfo"+data);

        HashMap<String,String> stringHashMap = session.getUserDetails();
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setUserName(stringHashMap.get("userName"));
        userRegistration.setMobileNo(stringHashMap.get("mobileNo"));
        userRegistration.setDepartmentName(stringHashMap.get("departmentName"));
        adminMobNo = stringHashMap.get("adminMobNo");

        //LoadingDialog.show(this, getString(R.string.RegisterDialogMessage));
        try {

            AsyncLoader.load(new AsyncLoader.SyncBlock()
            {
                @Override
                public void run() throws Exception
                {

                    Boolean isUninstalled = RegistrationService.sharedInstance().appUninstall(userRegistration);
                    if(isUninstalled){
                        AsyncLoader.loadOnUIThread(new AsyncLoader.CompletionBlock()
                        {
                            @Override
                            public void run(Exception exception)
                            {
                                //LoadingDialog.dismiss();
                                Toast toast = Toast.makeText(context, "You have violated the bitwise security policies and it is notified to management. Kindly contact concerned authority immediately.", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        });
                    }
                }
            }, new AsyncLoader.CompletionBlock() {
                @Override
                public void run(Exception exception) {
                    LoadingDialog.dismiss();
                    Toast.makeText(context, "Network Error "+exception.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.i("MSA","isregisteredException "+e.toString());
            e.printStackTrace();
        }


        finally {
            SendUnInstallationSMS(context, userRegistration.getUserName(), userRegistration.departmentName, userRegistration.mobileNo,adminMobNo);
        }
    }


    private void SendUnInstallationSMS(Context context, String UserName, String CompanyName, String MobileNo, String adminMobNo) {

        try {
            String SMSAndPushData = context.getResources().getString(R.string.DataForPushAndSMS);
            SMSAndPushData = SMSAndPushData.replace("[UserName]", UserName);
            SMSAndPushData = SMSAndPushData.replace("[Department]", CompanyName);
            SMSAndPushData = SMSAndPushData.replace("[Mobile]", MobileNo);

            new SecuritySMS(context).sendSms(adminMobNo, SMSAndPushData, false);
            Toast.makeText(context, "SMS sent.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            dbObj.PutLog("[@@mDPR]SMS "+e.toString());
        }
    }


    @Override
    public void onEnabled(Context context, Intent intent) {
        try {
            dbObj = new MSASQLiteDB(context);
            //TODO check Y this ApplicationStatus is needed dbObj.AddApplicationStatusInfo(1);
            //Till now we do not find any use of Table ApplicationStatus
            Toast.makeText(context, "Bitwise's Device Admin is now enabled", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            dbObj.PutLog("[@@mDPR]EX onEnabled-"+e);
        }
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        CharSequence disableRequestedSeq = "Requesting to disable Device Admin";

        return disableRequestedSeq;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
