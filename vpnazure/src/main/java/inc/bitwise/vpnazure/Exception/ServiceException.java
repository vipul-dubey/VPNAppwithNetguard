package inc.bitwise.vpnazure.Exception;

/**
 * Created by Vipuld on 16/09/2019.
 */

public class ServiceException extends Exception {

    public int ErrorCode;

    public ServiceException(String message, int ErrorCode)
    {
        super(message);
        this.ErrorCode=ErrorCode;
    }

}
