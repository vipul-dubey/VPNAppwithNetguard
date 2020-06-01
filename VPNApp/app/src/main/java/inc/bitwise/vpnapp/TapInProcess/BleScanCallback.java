package inc.bitwise.vpnapp.TapInProcess;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.util.Log;

import java.util.List;

import inc.bitwise.vpnapp.ActivityMain;
import inc.bitwise.vpnapp.CommonUtils.LoadingDialog.LoadingDialog;
import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;
import inc.bitwise.vpnapp.abstractclasses.BleAbstract;

/**
 * Created by vipuld on 9/7/2016.
 */
@TargetApi(21)
public class BleScanCallback extends ScanCallback {

    private BleAbstract connectToDeviceHandler;
    TapIn mainActivity = new TapIn();

    MSASQLiteDB dbObj;
    Context context;

    public BleScanCallback(BleAbstract handler, Context ctx)
    {
        context = ctx;
        dbObj = new MSASQLiteDB(ctx);
        connectToDeviceHandler = handler;

    }

    public void OnConnectToDevice(final BluetoothDevice device, boolean proceed) {
       // Log.i("MSA","Setup on MAin for CALL BACK -----")
       // mainActivity.processCompleted = true;
        Log.i("MSA","$$4 Check BOOL - " + mainActivity.processCompleted);
        if(!mainActivity.processCompleted) {
            mainActivity.processCompleted = true;
            mainActivity.connectToDevice(device);
        }
    }

    /**
     * Callback when a BLE advertisement has been found.
     *
     * @param callbackType Determines how this callback was triggered. Could be one of
     *            {@link ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
     *            {@link ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
     *            {@link ScanSettings#CALLBACK_TYPE_MATCH_LOST}
     * @param result A Bluetooth LE scan result.
     */
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        try {
            //dbObj = new MSASQLiteDB(context);
            double distance = getDistance(result.getRssi());

            if (distance < 1) {
                //  Log.i("MSA","BLE Distance Validated");
                BluetoothDevice btDevice = result.getDevice();

                String deviceName =  result.getDevice().getName();
                String UDID=result.getDevice().getAddress();
                Log.i("MSA","UDID " +UDID);
                ActivityMain.BtAddress=UDID;
                ActivityMain.facility=deviceName;
                // Log.i("MSA","## Name - " + deviceName);
                if (btDevice != null ) {

                    if (connectToDeviceHandler != null ) {
                        Log.i("MSA","BLE OnConnet TO Device");

                        if(deviceName.contains("Msa") ) {
                            //   Log.i("MSA","Device Matched");
                            if(!TapIn.processCompleted) {
                                //TODO Clear this
                                //TapIn.textBLERange.setText(R.string.Processing);
                                LoadingDialog.progressDialog.setMessage("Processing");
                                OnConnectToDevice(btDevice, true);
                            }
                        }
                        else
                        {
                            Log.i("MSA","Device Not Matched");
                            // Log.i("MSA","deviceName --- " + deviceName);
                        }
                    }
                } else {
                    dbObj.PutLog("btDevice Null");
                }

            } else {
                String textCheck =  TapIn.textBLERange;

                // Log.i("MSA","BLE Show Out of Range");
                dbObj.PutLog("[LL]textCheck-"+textCheck);
                if(!textCheck.contains("Processing")) {
                    dbObj.PutLog("[LL]Show Out of Range");

                    //MainActivity.textBLERange.setText("Out of  Range!");
                    LoadingDialog.progressDialog.setMessage("Out of  Range!");
                }
                else
                {
                    dbObj.PutLog("[LL]text equales Processing");
                }
            }
        } catch (Exception ex) { dbObj.PutLog("[@@SCB]Ex1" + ex.toString());
        }
    }

    /**
     * Callback when batch results are delivered.
     *
     * @param results List of scan results that are previously scanned.
     */
    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        for (ScanResult sr : results) {
            dbObj.PutLog("[SCB]ScanResult - Results" + sr.toString());
        }
    }

    /**
     * Callback when scan could not be started.
     *
     * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
     */
    @Override
    public void onScanFailed(int errorCode) {
        dbObj.PutLog("[SCB]Scan Failed Error Code: " + errorCode);
    }

    double getDistance(int rssi) {
        return Math.pow(10d, ((double)  -52 - rssi) / (10 * 2));
    }
}

