package inc.bitwise.vpnazure.Models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class TapInOut {
    public String userName;
    public String tapAction;

    public TapInOut() {

    }

    public TapInOut(JSONObject data) {
        try {

            userName = data.getString("userName");
            tapAction = data.getString("tapAction");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("MSA","UserRegistration "+e.toString());
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();

        data.put("Username", userName);
        data.put("tapAction", tapAction);
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

    public String getTapValue()
    {
        return tapAction;
    }

    public void setTapValue(String tapValue)
    {
        this.tapAction = tapValue;
    }
}