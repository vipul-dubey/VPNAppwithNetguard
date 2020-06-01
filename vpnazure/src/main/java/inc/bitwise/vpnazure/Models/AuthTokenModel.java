package inc.bitwise.vpnazure.Models;

public class AuthTokenModel {
    public String authToken;

    public void AuthTokenModel(String _authToken){
        this.authToken = _authToken;
    }

    public String getAuthToken()
    {
        return this.authToken;
    }
    public void setAuthToken(String auth_Token){
        this.authToken =auth_Token;
    }

}
