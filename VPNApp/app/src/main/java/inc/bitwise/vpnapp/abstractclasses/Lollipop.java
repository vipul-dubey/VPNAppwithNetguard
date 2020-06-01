package inc.bitwise.vpnapp.abstractclasses;


import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import inc.bitwise.vpnapp.CommonUtils.CommonUtils;
import inc.bitwise.vpnapp.CommonUtils.LoadingDialog.LoadingDialog;
import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;
import inc.bitwise.vpnapp.TapInProcess.BleScanCallback;
import inc.bitwise.vpnapp.TapInProcess.TapIn;

/**
 * Created by chhayas on 8/10/2016.
 */
public class Lollipop extends AndroidVersionRepository {
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private BleScanCallback mScanCallback;
    private BluetoothLeScanner mLEScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private Runnable runnable;
    private static final long SCAN_PERIOD = 500;

    TapIn mainActivity = null;
    MSASQLiteDB dbObj;


    public final static UUID UUID_MSA_APP =  UUID.fromString("13333333-3333-3333-3333-333333333337");
    @Override
    public boolean isMobileDataDisabled(Context ctx) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, Exception {
        try {
            CommonUtils obj = new CommonUtils(ctx);
            dbObj = new MSASQLiteDB(ctx);
            return true;

        } catch (Exception e) {
            dbObj.PutLog("[@Lollipop]EX1"+e.toString());
        }
        return true;
    }

    @Override
    public boolean CheckRequiredPermissionsByApp(Activity nfcClass, Context ctx){
        return false;
    }

    @Override
    public void WakePermissionCheck(Context ctx) {
        //BLANK as not required of Lollipop
    }

    @Override
    public void StopServicesAtTAPOutAsPerOSVersion(Context ctx) {
        try {
			dbObj = new MSASQLiteDB(ctx);
            CommonUtils util = new CommonUtils(ctx);
            util.StopServicesAtOutAndIcon();
        } catch (Exception ex) {
            dbObj.PutLog("[@Lollipop]EX2 "+ex.toString());
        }
    }

    @Override
    public void DisplayPopUpForRequiredPermission(Activity activityBitwiseAdmin, Context ctx) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean SetBluetoothProperties(Context ctx,boolean ProcessDone) {
        try {
            dbObj = new MSASQLiteDB(ctx);
            //initialization of Bluetooth Adapter and BluetoothLeScanner
            dbObj.PutLog("[Lpop]init BLE prop");
            Log.i("MSA","#### SetBluetoothProperties");
            if(ProcessDone)
            {
                mHandler = new Handler();
                mHandler.removeCallbacksAndMessages(null);
                if (mGatt != null) {
                    Log.i("MSA","Disconnect");
                    dbObj.PutLog("[Lpop]set mGatt Null");
                    mGatt.disconnect();
                    mGatt.close();
                    mGatt = null;
                }

                return  true;

            }



            final BluetoothManager bluetoothManager =
                    (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mGatt != null) {
                dbObj.PutLog("[Lpop]set mGatt Null");
                mGatt.disconnect();
                mGatt.close();
                mGatt = null;
            }
            if (mBluetoothAdapter != null) {
                Log.i("MSA","SEEEEETTTTT");
                dbObj.PutLog("[Lpop]mBluetoothAdapter != null");
                mScanCallback = new BleScanCallback(connectToDeviceCallback, ctx);
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();

                //filter to scan specific serviceUUIDs
                ScanFilter filter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID_MSA_APP)).build();

                filters = new ArrayList<>();
                filters.add(filter);
                return true;
            }
            else {
                dbObj.PutLog("[Lpop]mBluetoothAdapter is NULL");
            }
        } catch (Exception ex) {
            dbObj.PutLog("[@Lpop]EX3 " + ex.toString());
        }
        return false;

    }
    private final BleAbstract connectToDeviceCallback = new BleAbstract()  {
        @Override
        public void OnConnectToDevice(final BluetoothDevice device){
                mainActivity = new TapIn();
                mainActivity.connectToDevice(device);
        }
    };

    public void StopScan()
    {
        try

        {
            mHandler = new Handler();
            Log.i("MSA", "mHandler Stopped");
            mHandler.removeCallbacksAndMessages(null);
        }
        catch(Exception ex) {
            Log.i("MSA", "@@@@ --- " + ex.toString());

    }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    //Start and Stop Scanning nearby Bluetooth Low Energy device with Bluetooth LE scan
    public boolean SetScanLeProperties(final boolean enable, BluetoothAdapter bleAdapter, Context ctx, TapIn MainAct,boolean processDone){
        try {

            Log.i("MSA"," in SetScanLePRoperties");

            if(processDone)
            {

                Log.i("MSA","STOPING SCAN #1");
               // mHandler.removeCallbacksAndMessages(null);
                if(mLEScanner !=null)
                {

                    Log.i("MSA","STOPING SCAN #2");
                    mLEScanner.stopScan(mScanCallback);
                    if (mGatt != null) {
                        Log.i("MSA","[Lpop] mLEScanner STOPPPP SCAN ");
                        dbObj.PutLog("[Lpop]On STOP Scan set mGatt Null");
                        mGatt.disconnect();
                        mGatt.close();
                        mGatt = null;
                    }
                }

            }
            else {

                mHandler = new Handler();
                dbObj = new MSASQLiteDB(ctx);
                if (mLEScanner != null) {
                    Log.i("MSA", "mLEScanner If");
                    if (enable) {
                        mHandler.postDelayed(new Runnable() {
                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void run() {
                                dbObj.PutLog("[Lpop] mLEScanner STOP SCAN");
                                mLEScanner.stopScan(mScanCallback);
                                //Todo clear this
                                //TapIn.textBLERange.setText("");
                                LoadingDialog.dismiss();
                                //LoadingDialog.progressDialog.setMessage("");

                                if (mGatt != null) {
                                    Log.i("MSA", "[Lpop] mLEScanner STOPPPP SCAN ");
                                    dbObj.PutLog("[Lpop]On STOP Scan set mGatt Null");
                                    mGatt.disconnect();
                                    mGatt.close();
                                    mGatt = null;
                                }
                            }
                        }, SCAN_PERIOD);

                        dbObj.PutLog("[Lpop] mLEScanner START SCAN");
                        Log.i("MSA", "[Lpop] mLEScanner START SCAN -set Processing");
                        if (!processDone) {
                            mLEScanner.startScan(filters, settings, mScanCallback);
                            //todo clear this
                            //TapIn.textBLERange.setText(R.string.Processing);
                            LoadingDialog.progressDialog.setMessage("Processing");
                        }
                    } else {
                        mLEScanner.stopScan(mScanCallback);
                        Log.i("MSA", "[Lpop] set MAIN Try AGAIN ");
                        //todo clear this
                        //TapIn.textBLERange.setText("Try Again");
                        LoadingDialog.progressDialog.setMessage("Try Again");

                    }
                } else {
                    Log.i("MSA", "mLEScanner ELSE");
                    dbObj.PutLog("[Lpop]Object is null !!");
                    SetBluetoothProperties(ctx, false);
                    if (mLEScanner != null) {
                        if (enable) {
                            mHandler.postDelayed(new Runnable() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void run() {
                                    dbObj.PutLog("[Lpop] mLEScanner STOP SCAN");
                                    Log.i("MSA", "[Lpop] mLEScanner STOP SCAN #2");
                                    mLEScanner.stopScan(mScanCallback);
                                    //Todo clear this
                                    //TapIn.textBLERange.setText("");
                                    LoadingDialog.dismiss();
                                    //LoadingDialog.progressDialog.setMessage("");
                                    if (mGatt != null) {
                                        dbObj.PutLog("[Lpop]On STOP Scan set mGatt Null");
                                        mGatt.disconnect();
                                        mGatt.close();
                                        mGatt = null;
                                    }
                                }
                            }, SCAN_PERIOD);
                            Log.i("MSA", "[Lpop] mLEScanner START SCAN #2");
                            dbObj.PutLog("[Lpop] mLEScanner START SCAN");
                            if (!processDone) {
                                mLEScanner.startScan(filters, settings, mScanCallback);
                            } else {
                                mLEScanner.stopScan(mScanCallback);
                            }
                        } else {
                            dbObj.PutLog("[Lpop] mLEScanner STOP SCAN");
                            mLEScanner.stopScan(mScanCallback);
                        }
                    }
                }
            }
        }
        catch(Exception ex){
			dbObj.PutLog("[@Lpop]EX4 " + ex.toString());
        }
        return true;
    }

    @Override
    public boolean BluetoothPermissionAtCheckInOut(Activity maBleClass, Context ctx){
        return false;
    }

    @Override
    public boolean SetActivityContentView(Activity maBleClass, Context ctx) {
        //maBleClass.setContentView(R.layout.otp_authentication);
        return false;
    }


}
