package inc.bitwise.vpnazure.Services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import inc.bitwise.vpnazure.Models.AuthTokenModel;

import static inc.bitwise.vpnazure.Utils.Endpoint.userRegister;

public class GetAuthTokenService {
    private static GetAuthTokenService instance;
    public static GetAuthTokenService sharedInstance()
    {
        return instance;
    }
    private static Service service = Service.sharedInstance();


    /**
     * Get AuthToken Api Call
     *
     * @return Responce with Validation and Error if any.
     * @throws IOException
     * @throws JSONException
     * @throws Exception
     */
    public static boolean getAuthToken(AuthTokenModel authTokenModel) throws Exception
    {
        JSONObject data = service.sendGet(userRegister,authTokenModel);
        return false;
    }
}
