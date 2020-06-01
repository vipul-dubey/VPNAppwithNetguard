package inc.bitwise.vpnazure.Services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import inc.bitwise.vpnazure.Models.UserRegistration;

import static inc.bitwise.vpnazure.Utils.Endpoint.appNotification;
import static inc.bitwise.vpnazure.Utils.Endpoint.userRegister;
import static inc.bitwise.vpnazure.Utils.Endpoint.userUninstall;

/**
 * Created by Vipuld on 16/09/2019.
 */

public class RegistrationService {
    static boolean isRegistered;
    private static RegistrationService instance;
    public static RegistrationService sharedInstance()
    {
        return instance;
    }
    private static Service service = Service.sharedInstance();

    /**
     * Register User Api Call
     *
     * @return Responce with Validation and Error if any.
     * @throws IOException
     * @throws JSONException
     * @throws Exception
     */
    public static JSONObject registerUser(UserRegistration userRegistration) throws Exception
    {
        JSONObject data = service.sendPost(userRegister,userRegistration.toJSON().toString());
        System.out.print(data);

        if(data.has("error"))
        {
            isRegistered=false;
        }
        else{
            isRegistered=true;
        }
        return data;
    }

    public static boolean appUninstall(UserRegistration userRegistration) throws Exception
    {
        JSONObject data = service.sendPost(userUninstall,userRegistration.toJSONUninstall().toString());
        if(data.has("error"))
        {
            isRegistered=false;
        }
        else{
            isRegistered=true;
        }
        return isRegistered;
    }

    public static boolean appNotification(UserRegistration userRegistration,String EmailSubject) throws Exception
    {
        JSONObject data = service.sendPost(appNotification,userRegistration.toJSONNotification(EmailSubject).toString());
        if(data.has("error"))
        {
            isRegistered=false;
        }
        else{
            isRegistered=true;
        }
        return isRegistered;
    }

}
