package inc.bitwise.vpnazure.Models;

/**
 * Created by Vipuld on 16/09/2019.
 */

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class UserRegistration implements Serializable {
    public String userName;
    public String mobileNo;
    public String aadharId;
    public String departmentName;

    public UserRegistration() {

    }

    public UserRegistration(JSONObject data) {
        try {

            userName = data.getString("userName");
            mobileNo = data.getString("mobileNo");
            aadharId = data.getString("aadharId");
            departmentName = data.getString("departmentName");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("MSA","UserRegistration "+e.toString());
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();

        data.put("Username", userName);
        data.put("MobileNum", mobileNo);
        data.put("AadharID", aadharId);
        data.put("DepartmentName", departmentName);
        data.put("deviceType","Android");

        return data;
    }

    public JSONObject toJSONUninstall() throws JSONException {
        JSONObject data = new JSONObject();

        data.put("Username", userName);
        data.put("MobileNum", mobileNo);
        data.put("DepartmentName", departmentName);
        data.put("deviceType","Android");

        return data;
    }

    public JSONObject toJSONNotification(String EmailSubject) throws JSONException {
        JSONObject data = new JSONObject();

        data.put("Username", userName);
        data.put("MobileNum", mobileNo);
        data.put("Department", departmentName);
        data.put("EmailSubject",EmailSubject);

        return data;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String _userName)
    {
        this.userName = _userName;
    }

    public String getAadharId()
    {
        return aadharId;
    }

    public void setAadharId(String _aadharId)
    {
        this.aadharId = _aadharId;
    }

    public String getMobileNo()
    {
        return mobileNo;
    }

    public void setMobileNo(String _mobileNo)
    {
        this.mobileNo = _mobileNo;
    }

    public String getDepartmentName()
    {
        return departmentName;
    }

    public void setDepartmentName(String _departmentName)
    {
        this.departmentName = _departmentName;
    }


}