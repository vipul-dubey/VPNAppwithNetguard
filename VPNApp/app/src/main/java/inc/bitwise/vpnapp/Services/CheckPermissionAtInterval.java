package inc.bitwise.vpnapp.Services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.widget.Toast;


import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import inc.bitwise.vpnapp.CommonUtils.CommonUtils;
import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;

/**
 * Created by sakshit on 3/10/2016.
 */
public class CheckPermissionAtInterval extends AsyncTask<Void,Void,String> {

    Context localContext = null;
    MSASQLiteDB objDBHelper = null;

    public CommonUtils commonUtilityObj;

    public CheckPermissionAtInterval(Context ctx )
    {
        localContext = ctx;
        objDBHelper  = new MSASQLiteDB(localContext);
    }

    @Override
    protected String doInBackground(Void... params) {

        objDBHelper.PutLog("[ChkPerAtInt] Inside");
        Check_Permissions();
        return null;
    }

    public void NotifyMSAOnSMSPermissionDisable() {
        try {
            ArrayList<String> data = objDBHelper.getAdminInfo();
            commonUtilityObj = new CommonUtils(localContext);
            objDBHelper.PutLog("[MSA]sending email");
            //commonUtilityObj.violationNotificationToAzure(localContext, "SMSPermissionDisabled", data,"");

            Toast.makeText(localContext, "Mail sent", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            objDBHelper.PutLog("[@chckPermission]CheckPermission "+e.toString());
        }
    }
    public void Check_Permissions()
    {
        try {
            boolean permissionOff = false;

            if (ContextCompat.checkSelfPermission(localContext, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                // No explanation needed, we can request the permission.
              //  NFCActivity nfcActivityObj = new NFCActivity();
                //nfcActivityObj.NotifyMSAOnSMSPermissionDisable();
                objDBHelper.PutLog("[MSA]NotifyMSAOnSMSPermissionDisable from Receiver");
                //NotifyMSAOnSMSPermissionDisable();
               // nfcActivityObj.finish();
                permissionOff = true;
            }

            if (ContextCompat.checkSelfPermission(localContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissionOff = true;
            }

            if (ContextCompat.checkSelfPermission(localContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                permissionOff = true;
            }

            if (ContextCompat.checkSelfPermission(localContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionOff = true;
            }

            if(permissionOff)
            {
                objDBHelper.PutLog("[ChkPerAtInt]permissionOff");
            }

        }
        catch (Exception e)
        {
            objDBHelper.PutLog("[ChkPerAtInt EX]EX1 " + e.toString());
        }
    }



}