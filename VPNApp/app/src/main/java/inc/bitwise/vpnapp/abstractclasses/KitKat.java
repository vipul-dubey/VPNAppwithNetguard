package inc.bitwise.vpnapp.abstractclasses;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;

import inc.bitwise.vpnapp.ActivityMain;
import inc.bitwise.vpnapp.CommonUtils.CommonUtils;
import inc.bitwise.vpnapp.CommonUtils.LoadingDialog.LoadingDialog;
import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;
import inc.bitwise.vpnapp.TapInProcess.TapIn;

/**
 * Created by chhayas on 8/10/2016.
 */
public class KitKat extends AndroidVersionRepository {
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 2000;
    TapIn mainActivity = null;
    private static boolean ScanStatus = false;
    MSASQLiteDB dbObj;

    @Override
    public boolean isMobileDataDisabled(Context ctx) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, Exception {
        CommonUtils obj = new CommonUtils(ctx);
        dbObj = new MSASQLiteDB(ctx);
        return true;
    }

    @Override
    public boolean CheckRequiredPermissionsByApp(Activity nfcClass, Context ctx){
        return false;
    }

    public  void StopScan()
    {}
    @Override
    public void WakePermissionCheck(Context ctx) {
        //BLANK as not required of KitKat
    }

    @Override
    public void StopServicesAtTAPOutAsPerOSVersion(Context ctx)throws Exception {

        MSASQLiteDB dbObj = new MSASQLiteDB(ctx);
        try {
            CommonUtils util = new CommonUtils(ctx);
            util.StopServicesAtOutAndIcon();
        } catch (Exception ex) {
            dbObj.PutLog("[KitKat]EX "+ex.toString());
        }
    }

    @Override
    public void DisplayPopUpForRequiredPermission(Activity activityBitwiseAdmin, Context ctx) {

    }

    @Override
    public boolean SetBluetoothProperties(Context ctx,boolean processDone){
        //BLANK as not required of KitKat
        return true;
    }

    @Override
    //Start and Stop Scanning nearby Bluetooth Low Energy device
    public boolean SetScanLeProperties(final boolean enable, final BluetoothAdapter bleAdapter, Context ctx, TapIn MainAct,boolean processDone){

        try {
            mainActivity = (TapIn) MainAct;
            dbObj = new MSASQLiteDB(ctx);
            if (bleAdapter != null) {
                ScanStatus = enable;
                if (enable) {
                    mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bleAdapter.stopLeScan(mLeScanCallback);
                            //Todo clear this
                            //TapIn.textBLERange.setText("");
                            LoadingDialog.progressDialog.setMessage("");
                            dbObj.PutLog("[KitKat]mBluetoothAdapter STOP SCAN");
                        }
                    }, SCAN_PERIOD);
                    bleAdapter.startLeScan(mLeScanCallback);
                    dbObj.PutLog("[KitKat]mBluetoothAdapter START SCAN");
                } else {
                    bleAdapter.stopLeScan(mLeScanCallback);
                }
            } else {
                dbObj.PutLog("[KitKat]Object is null");
            }
        }catch(Exception ex){
            dbObj.PutLog("[KitKat]EX2 " + ex.toString());
        }
        return true;
    }

    //Callback reporting an LE device found during a device scan initiated
    // by the {BluetoothAdapter#startLeScan} function.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     byte[] scanRecord) {
                    try {
                        mainActivity=new TapIn();
                    if(ScanStatus) {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                double distance = getDistance(rssi);
                                if (distance < 1) {
                                   ActivityMain.BtAddress= device.getAddress();
                                    if(device.getName().contains("Msa")) {
                                        if(!mainActivity.processCompleted)
                                        {
                                            mainActivity.processCompleted=true;
                                            mainActivity.connectToDevice(device);
                                            //Todo Clear this
                                            //TapIn.textBLERange.setText(R.string.Processing);
                                            LoadingDialog.progressDialog.setMessage("Processing");
                                        }

                                    }
                                }
                                else{
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Todo clear this
                                            //TapIn.textBLERange.setText("Out of  Range!");
                                            LoadingDialog.progressDialog.setMessage("Out of  Range!");
                                        }
                                    }, 4000);

                                }
                            }
                        });
                        }
                    }
                    catch(Exception ex){
                        dbObj.PutLog("[KitKat]EX3 " + ex.toString());
                    }
                }
            };

    double getDistance(int rssi) {
        return Math.pow(10d, ((double)  -52  - rssi) / (10 * 2));
    }


    @Override
    public boolean BluetoothPermissionAtCheckInOut(Activity maBleClass, Context ctx) {
        return false;
    }

    @Override
    public boolean SetActivityContentView(Activity maBleClass, Context ctx) {
        //maBleClass.setContentView(R.layout.otp_authentication_kikat);
        return false;
    }

}
