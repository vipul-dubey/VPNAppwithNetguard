package inc.bitwise.vpnazure.Services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import inc.bitwise.vpnazure.Models.TapInOut;

import static inc.bitwise.vpnazure.Utils.Endpoint.userTapInOut;
/**
 * Created by Vipuld on 16/09/2019.
 */

public class TapInService {

    static boolean isTapInOut;
    private static TapInService instance;
    public static TapInService sharedInstance()
    {
        return instance;
    }
    private static Service service = Service.sharedInstance();

    /**
     * TapInOut  Api Call
     *
     * @return Responce with Validation and Error if any.
     * @throws IOException
     * @throws JSONException
     * @throws Exception
     */
    public static JSONObject tapInOutUser(TapInOut tapInOut) throws Exception
    {
        JSONObject data = service.sendPost(userTapInOut,tapInOut.toJSON().toString());
        System.out.print(data);

        if(data.has("error"))
        {
            isTapInOut=false;
        }
        else{
            isTapInOut=true;
        }
        return data;
    }
}
