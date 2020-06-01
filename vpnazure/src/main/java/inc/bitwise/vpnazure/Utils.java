package inc.bitwise.vpnazure;

import java.util.ArrayList;
import java.util.Arrays;

import static inc.bitwise.vpnazure.Utils.Endpoint.appNotification;
import static inc.bitwise.vpnazure.Utils.Endpoint.userRegister;
import static inc.bitwise.vpnazure.Utils.Endpoint.userUninstall;
import static inc.bitwise.vpnazure.Utils.Endpoint.userTapInOut;


/**
 * Created by Vipuld on 16/09/2019.
 */

public class Utils {

    public enum Endpoint {
        appNotification("/visitor/notificationvpn"),
        userRegister("/visitor/registervpn"),
        userUninstall("/visitor/uninstallvpn"),
        userTapInOut("/visitor/vpntapin_out");
        private final String value;

        Endpoint(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    public static final ArrayList<Endpoint> RetryEndpoints = new ArrayList<>(Arrays.asList(
            appNotification,userRegister,userUninstall,userTapInOut));
}