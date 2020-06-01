package inc.bitwise.vpnapp.abstractclasses;


import android.Manifest.permission;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import inc.bitwise.vpnapp.CommonUtils.CommonUtils;
import inc.bitwise.vpnapp.CommonUtils.LoadingDialog.LoadingDialog;
import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;
import inc.bitwise.vpnapp.Receivers.CheckPermissionReceiver;
import inc.bitwise.vpnapp.TapInProcess.BleScanCallback;
import inc.bitwise.vpnapp.TapInProcess.TapIn;

/**
 * Created by chhayas on 8/10/2016.
 */
public class Marshmallow extends AndroidVersionRepository {

    private  static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 3;
    private static  final int MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE = 4;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private BleScanCallback mScanCallback;
    private BluetoothLeScanner mLEScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 2000;
    public CommonUtils commonUtilityObj;
    TapIn mainActivity = null;

    public static Context marshmallowContext = null;
    BroadcastReceiver IReceiver = null;
    public final static UUID UUID_MSA_APP =  UUID.fromString("13333333-3333-3333-3333-333333333337");

    MSASQLiteDB dbObj;

    @Override
    public boolean isMobileDataDisabled(Context ctx) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, Exception {
        try {
            CommonUtils obj = new CommonUtils(ctx);
            dbObj = new MSASQLiteDB(ctx);
            return true;

        } catch (Exception e) {
            dbObj.PutLog("[@Marsh]EX1"+e.toString());
        }
        return true;
    }


    @Override
    public void WakePermissionCheck(Context ctx) {
        try {
            dbObj = new MSASQLiteDB(ctx);
            dbObj.PutLog("[NFC]WakePermissionCheck");
            int alarm_code = 1234567;
            Date currentDate = new Date(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            // _BitwiseDBHelper.PutLog("[NFC]Time set 6 pm" +calendar.getTimeInMillis());
            Intent myIntent = new Intent(ctx, CheckPermissionReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, alarm_code, myIntent, 0);
            AlarmManager alarmManager = (AlarmManager)ctx.getSystemService(ctx.ALARM_SERVICE);
            // alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, TimerPermissionService, TimerPermissionService,pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 14400000, 14400000, pendingIntent);
            if (Build.VERSION.SDK_INT > 23)
                RegisterInternetReceiverForNougat(ctx);
        } catch (Exception e) {
            dbObj.PutLog("[@Marsh]EX2"+e.toString());
        }
    }

    private void RegisterInternetReceiverForNougat(Context context)
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //Todo check this
        //IReceiver = new InternetReceiver();
        context.registerReceiver(IReceiver, filter);
        dbObj.PutLog("[Marsh] IR register for N");
    }

    @Override
    public void StopServicesAtTAPOutAsPerOSVersion(Context ctx) {
        try {
            dbObj = new MSASQLiteDB(ctx);
            int alarm_code = 1234567;
            CommonUtils util = new CommonUtils(ctx);
            //Todo check this
            util.StopServicesAtOutAndIcon();

            Intent myIntent = new Intent(ctx, CheckPermissionReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, alarm_code, myIntent, 0);
            AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ctx.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        } catch (Exception ex) {
            dbObj.PutLog("[@Marsh]EX3"+ex.toString());
        }
    }

    @Override
    public boolean CheckRequiredPermissionsByApp(Activity nfcClass, Context ctx)
    {
        boolean permissionOff = false;
		dbObj = new MSASQLiteDB(ctx);
        try {


            if (ContextCompat.checkSelfPermission(ctx, permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(nfcClass, new String[]{permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                NotifyMSAOnSMSPermissionDisable(ctx);
                permissionOff = true;
                dbObj.PutLog("[Marsh] SMS");
            }

            if (ContextCompat.checkSelfPermission(ctx, permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(nfcClass, new String[]{permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                permissionOff = true;
                dbObj.PutLog("[Marsh]Phone state");
            }

            if (ContextCompat.checkSelfPermission(ctx, permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(nfcClass, new String[]{permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                permissionOff = true;
                dbObj.PutLog("[Marsh]Contacts");
            }

            if (ContextCompat.checkSelfPermission(ctx, permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(nfcClass, new String[]{permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE);
                permissionOff = true;
            }

            if(permissionOff)
            {
                Toast.makeText(ctx, "[SMS][Phone][Contact][Storage]Permissions are mandatory, don't Deny", Toast.LENGTH_LONG).show();

            }

        }
        catch (Exception e)
        {
            dbObj = new MSASQLiteDB(ctx);
            dbObj.PutLog("[@Marshmallow]EX4 " + e.toString());
        }
        return permissionOff;
    }

    @Override
    public void DisplayPopUpForRequiredPermission(Activity activityBitwiseAdmin, Context ctx) {
        try {


            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, android.Manifest.permission.SEND_SMS,ctx))
                permissionsNeeded.add("SMS");

            if (!addPermission(permissionsList, android.Manifest.permission.READ_PHONE_STATE,ctx))
                permissionsNeeded.add("Read phone state");

            if (!addPermission(permissionsList, android.Manifest.permission.READ_CONTACTS,ctx))
                permissionsNeeded.add("Write Contacts");

            if (!addPermission(permissionsList, android.Manifest.permission.READ_EXTERNAL_STORAGE,ctx))
                permissionsNeeded.add("Read Ext Storage");


            if (permissionsList.size() > 0) {

                if (permissionsNeeded.size() > 0) {
                    ActivityCompat.requestPermissions(activityBitwiseAdmin, permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    return;
                }
            }

        }
        catch (Exception ex)
        {
            dbObj = new MSASQLiteDB(ctx);
            dbObj.PutLog("[@Marshmallow]EX5 " + ex.toString());
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission, Context currentCtx) throws Exception {


        if (ContextCompat.checkSelfPermission(currentCtx, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean SetBluetoothProperties(Context ctx,boolean ProcessDone){
        dbObj = new MSASQLiteDB(ctx);
        try {


            dbObj.PutLog("[Mrsh]SetBluetoothProperties");
            //initialization of Bluetooth Adapter and BluetoothLeScanner
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mGatt != null) {
                dbObj.PutLog("[Mrsh]set mGatt Null");
                mGatt.close();
                mGatt = null;
            }
            dbObj.PutLog("[Mrsh]connectToDeviceCallback ");
            marshmallowContext = ctx;
            if (mBluetoothAdapter != null) {
                dbObj.PutLog("[Mrsh]mBluetoothAdapter != null");

            mScanCallback = new BleScanCallback(connectToDeviceCallback,ctx);
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();

            //filter to scan specific serviceUUIDs
            ScanFilter filter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID_MSA_APP)).build();

            filters = new ArrayList<>();

            filters.add(filter);
            dbObj.PutLog("[Mrsh]return true ");
            return true;
            }
            else {
                dbObj.PutLog("[Mrsh]mBluetoothAdapter is NULL");
            }
        }catch(Exception ex){
            dbObj.PutLog("[@Mrsh]EX6 " + ex.toString());
        }
        return false;
    }


        private final BleAbstract connectToDeviceCallback = new BleAbstract() {
        @Override
        public void OnConnectToDevice(BluetoothDevice device){
            mainActivity = new TapIn();
            mainActivity.connectToDevice(device);
        }
    };


    public void  StopScan()
    {}
		@TargetApi(Build.VERSION_CODES.M)
	    @Override
        //Start and Stop Scanning nearby Bluetooth Low Energy device with Bluetooth LE scan
		public boolean SetScanLeProperties(final boolean enable, BluetoothAdapter bleAdapter, Context ctx, final TapIn MainAct,boolean processDone){
        dbObj = new MSASQLiteDB(ctx);
        try {
            if(mLEScanner !=null) {
                if (enable) {
                    mHandler.postDelayed(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            dbObj.PutLog("[Mrsh] mLEScanner STOP SCAN");
                            mLEScanner.stopScan(mScanCallback);
                            //todo clear this
                            //TapIn.textBLERange.setText("");
                            LoadingDialog.dismiss();
                            //LoadingDialog.progressDialog.setMessage("");
                            if (mGatt != null) {
                                dbObj.PutLog("[Mrsh]On STOP Scan set mGatt Null");
                                mGatt.disconnect();
                                mGatt.close();
                                mGatt = null;
                            }
                        }
                    }, SCAN_PERIOD);

                    dbObj.PutLog("[Mrsh] mLEScanner START SCAN");
                    mLEScanner.startScan(filters, settings, mScanCallback);
                } else {
                    dbObj.PutLog("[Mrsh] mLEScanner STOP SCAN");
                    mLEScanner.stopScan(mScanCallback);
                }
            }
            else
            {
                dbObj.PutLog("[Mrsh] Object is Null !!Start Process Again");
                SetBluetoothProperties(ctx,false);
                if(mLEScanner !=null) {
                    if (enable) {
                        mHandler.postDelayed(new Runnable() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void run() {
                                dbObj.PutLog("[Mrsh] mLEScanner STOP SCAN");
                                mLEScanner.stopScan(mScanCallback);
                                //todo clear this
                                //TapIn.textBLERange.setText("");
                                LoadingDialog.dismiss();
                                if (mGatt != null) {
                                    dbObj.PutLog("[Mrsh]On STOP Scan set mGatt Null");
                                    mGatt.disconnect();
                                    mGatt.close();
                                    mGatt = null;
                                }
                            }
                        }, SCAN_PERIOD);

                        dbObj.PutLog("[Mrsh] mLEScanner START SCAN");
                        mLEScanner.startScan(filters, settings, mScanCallback);
                    } else {
                        dbObj.PutLog("[Mrsh] mLEScanner STOP SCAN");
                        mLEScanner.stopScan(mScanCallback);
                    }
                }
            }

        }
        catch(Exception ex) {
            dbObj.PutLog("[@Mrsh]EX7 " + ex.toString());
        }
        return true;
    }

    @Override
    //Need ACCESS_COARSE_LOCATION permission for BLE Device scanning
    public boolean BluetoothPermissionAtCheckInOut(Activity maBleClass , Context ctx){

        try {
            dbObj = new MSASQLiteDB(ctx);
            final Activity actObj = maBleClass;

            if (ContextCompat.checkSelfPermission(actObj, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(actObj);
                builder.setTitle("This app needs Location access");
                builder.setMessage("Please grant it");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ActivityCompat.requestPermissions(actObj, new String[]{permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                });
                builder.show();
            }
        }catch(Exception Ex){
            dbObj.PutLog("[@Mrsh]EX8 " + Ex.toString());
        }
        return true;
    }

    @Override
    public boolean SetActivityContentView(Activity maBleClass, Context ctx) {
        //maBleClass.setContentView(R.layout.otp_authentication);

        return false;
    }

    public void NotifyMSAOnSMSPermissionDisable(Context localContext) {
        try {
            ArrayList<String> data = dbObj.getAdminInfo();
            commonUtilityObj = new CommonUtils(localContext);
            dbObj.PutLog("[MSA]sending email");
            //Todo Check this
            //commonUtilityObj.violationNotificationToAzure(localContext, "SMSPermissionDisabled", data,"");

            Toast.makeText(localContext, "Mail sent", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            Log.i("CheckPermissionAtInt", "EXXXX- " +e.toString());
        }
    }

}
