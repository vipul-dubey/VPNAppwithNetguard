package inc.bitwise.vpnapp.CommonUtils.Model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import inc.bitwise.vpnazure.Models.UserRegistration;

public class UserSessionManager {

    //public static final String KEY_EMAIL = "email";
    //public static final String KEY_NAME = "name";
    int PRIVATE_MODE = 0;
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;


    public UserSessionManager(Context paramContext)
    {
        this._context = paramContext;
        this.pref = this._context.getSharedPreferences("AndroidPref", this.PRIVATE_MODE);
        this.editor = this.pref.edit();
    }

    public void createUserLoginSession(UserRegistration userRegistration)
    {
        this.editor.putBoolean("IsUserLoggedIn", true);

        this.editor.putString("userName", userRegistration.getUserName());
        this.editor.putString("mobileNo", userRegistration.getMobileNo());
        this.editor.putString("aadharId", userRegistration.getAadharId());
        this.editor.putString("departmentName", userRegistration.getDepartmentName());

        this.editor.putString("adminMobNo","8269632336");
        //this.editor.putString("name", paramString1);
        //this.editor.putString("email", paramString2);
        this.editor.commit();
    }

    public void setScreenStatus()
    {
        this.editor.putBoolean("IsScreenOn", true);
        //this.editor.putString("name", paramString1);
        //this.editor.putString("email", paramString2);
        this.editor.commit();
    }

    /*public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> localHashMap = new HashMap<String, String>();
        localHashMap.put("name", this.pref.getString("name", null));
        localHashMap.put("email", this.pref.getString("email", null));
        return localHashMap;
    }*/

    public boolean isUserLoggedIn()
    {
        return this.pref.getBoolean("IsUserLoggedIn", false);
    }

    /*public void setUserDetails()
    {
        this.editor.putString("userName", userName);
        this.editor.putString("mobileNo", mobileNo);
        this.editor.putString("companyEmailAddress", companyEmailAddress);
        this.editor.putString("departmentName", departmentName);

        this.editor.commit();
    }*/


    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> userDetails = new HashMap<String, String>();
        userDetails.put("userName", this.pref.getString("userName", null));
        userDetails.put("mobileNo", this.pref.getString("mobileNo", null));
        userDetails.put("aadharId", this.pref.getString("aadharId", null));
        userDetails.put("departmentName", this.pref.getString("departmentName", null));
        userDetails.put("adminMobNo",this.pref.getString("adminMobNo", null));

        return userDetails;
    }

}
