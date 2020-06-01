package inc.bitwise.vpnapp.abstractclasses;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;

import inc.bitwise.vpnapp.TapInProcess.TapIn;


/**
 * Created by chhayas on 6/17/2016.
 */
public abstract class AndroidVersionRepository {

    public static AndroidVersionRepository newInstance() {

        if (Build.VERSION.SDK_INT >= 23) {
            return new Marshmallow();
        } else if (Build.VERSION.SDK_INT >=21) {
            return new Lollipop();
        } else {
            return new KitKat();
        }
    }

    public abstract boolean isMobileDataDisabled(Context ctx) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, Exception;

    public abstract void WakePermissionCheck(Context ctx);

    public abstract void StopServicesAtTAPOutAsPerOSVersion(Context ctx)throws Exception;

    public abstract boolean CheckRequiredPermissionsByApp(Activity nfcClass, Context ctx);

    public abstract void DisplayPopUpForRequiredPermission(Activity BAClass, Context ctx);

    public abstract boolean SetBluetoothProperties(Context ctx,boolean ProcessDone);

    public abstract boolean SetScanLeProperties(final boolean enable, BluetoothAdapter bleAdapter, Context ctx, TapIn MainAct,boolean processDone);

    public abstract boolean BluetoothPermissionAtCheckInOut(Activity maBleClass, Context ctx);

    public abstract boolean SetActivityContentView(Activity maBleClass, Context ctx);

    public abstract  void  StopScan();
}
