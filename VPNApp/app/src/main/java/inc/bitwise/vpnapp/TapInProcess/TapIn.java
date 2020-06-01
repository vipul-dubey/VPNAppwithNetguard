package inc.bitwise.vpnapp.TapInProcess;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.bitwise.vpnapp.ActivityMain;
import inc.bitwise.vpnapp.CommonUtils.CommonUtils;
import inc.bitwise.vpnapp.CommonUtils.DeviceAdminUtility;
import inc.bitwise.vpnapp.CommonUtils.LoadingDialog.LoadingDialog;
import inc.bitwise.vpnapp.CommonUtils.MSASQLiteDB;
import inc.bitwise.vpnapp.R;
import inc.bitwise.vpnapp.Registration.UserRegistration;
import inc.bitwise.vpnapp.abstractclasses.AndroidVersionRepository;

import static inc.bitwise.vpnapp.Registration.UserRegistration.SHARED_PREFS;
import static inc.bitwise.vpnapp.Registration.UserRegistration.Username;

public class TapIn extends AppCompatActivity {

    @BindView(R.id.TapIn)
    Button TapIn;

    @BindView(R.id.TapOut)
    Button TapOut;

    protected static final int REQUEST_ENABLE = 1;

    DeviceAdminUtility utilityObj;
    public static Context callBackContext = null;
    TextView txtMainMessage;
    TextView textTapInfo;
    //CommonUtils commonUtils;
    private BluetoothGatt mGatt;
    MSASQLiteDB dbObj;
    AndroidVersionRepository currentOS = null;
    private BluetoothAdapter mBluetoothAdapter;
    public TapIn(){}
    LoadingDialog loadingDialog;

    public static boolean processCompleted = false;
    public static Boolean mCheckInOutVal = null;
    public final static UUID UUID_MSA_APP =  UUID.fromString("13333333-3333-3333-3333-333333333337");

    public static String textBLERange = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tapinmain);
        ButterKnife.bind(this);
        initialise(this);

        Intent deviceAdminIntent = null;
        try {
            deviceAdminIntent = utilityObj.InstantiateDeviceAdminPolicyIntent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivityForResult(deviceAdminIntent, REQUEST_ENABLE);

        DisplayCameraStatus();


        currentOS.BluetoothPermissionAtCheckInOut(TapIn.this, this.getApplicationContext());

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

    }

    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
        loadingDialog.dismiss();
        finish();
    }

    private void initialise(Context context) {

        Resources resources = context.getResources();
        loadingDialog = new LoadingDialog();
        dbObj = new MSASQLiteDB(this.getApplicationContext());
        utilityObj = new DeviceAdminUtility(this.getApplicationContext());
        callBackContext = TapIn.this;
        txtMainMessage = (TextView) findViewById(R.id.txtMainMessage);
        textTapInfo = (TextView) findViewById(R.id.textTapInfo);
        currentOS = AndroidVersionRepository.newInstance();
        String azureHost = resources.getString(R.string.AzureHost);

    }

    private void DisplayCameraStatus() {
        try {

            if (utilityObj.GetCameraStatus()) {
                txtMainMessage.setText(getString(R.string.CameraDisable));
                textTapInfo.setText(getString(R.string.textInfoForTapOut));
                txtMainMessage.setTextColor(Color.parseColor("#ff0000"));
                txtMainMessage.setVisibility(View.VISIBLE);
            } else {
                txtMainMessage.setText(getString(R.string.CameraEnable));
                textTapInfo.setText(getString(R.string.textInfoForTapIn));
                txtMainMessage.setTextColor(Color.parseColor("#00ff18"));
                txtMainMessage.setVisibility(View.VISIBLE);
            }
        }

        catch (Exception ex)
        {
            dbObj.PutLog("[@@MA]EX DisplayCameraStatus "+ex.toString());
        }
    }

    @OnClick(R.id.TapIn)
    protected void tapInProcess(){
        try{
            Log.i("MSA","TapIN****&&&&&");
            loadingDialog.show(TapIn.this,"Searching For Bluetooth");
            //TODO delete this
            textBLERange = "Searching For Bluetooth";
            msaCheckInCheckout(true);

            //TODO delete this
            //TransitionManager.startActivity(this, ActivityMain.class);

        }catch(Exception e){
            Log.i("MSA","E"+e.toString());
        }
    }


    @OnClick(R.id.TapOut)
    protected void tapOutProcess(){
        try{

            Log.i("MSA","TapOut****&&&&&");
            loadingDialog.show(TapIn.this,"Searching For Bluetooth");
            //TODO delete this
            textBLERange = "Searching For Bluetooth";
            // textBLERange.setText(getString(R.string.SearchingBleDevice));
            msaCheckInCheckout(false);

            //TODO delete this
            //TransitionManager.startActivity(this, ActivityMain.class);

        }catch(Exception e){
            Log.i("MSA","E"+e.toString());
        }
    }

    //TODO delete this
    public class SubmitCheckIn implements View.OnClickListener{
        @Override
        public void onClick(View v){

            loadingDialog.show(TapIn.this,"Searching For Bluetooth");
            //textBLERange.setText(getString(R.string.SearchingBleDevice));
            msaCheckInCheckout(true);

        }

    }
    //TODO delete this
    public class SubmitCheckOut implements View.OnClickListener{
        @Override
        public void onClick(View v){
            loadingDialog.show(TapIn.this,"Searching For Bluetooth");
           // textBLERange.setText(getString(R.string.SearchingBleDevice));
            msaCheckInCheckout(false);
        }

    }

    public void msaCheckInCheckout(boolean isCheckIn) {
        mCheckInOutVal = isCheckIn;
        processCompleted = false;
        try {
            //currentOS = AndroidVersionRepository.newInstance();
            currentOS.DisplayPopUpForRequiredPermission(TapIn.this, this.getApplicationContext());

            if(mGatt != null)
            {
                //TODO delete this
               // textBLERange.setText("Try Again");
                textBLERange = "Try Again";
                loadingDialog.progressDialog.setMessage("Try Again");
                loadingDialog.progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingDialog.dismiss();
                    }
                });

                dbObj.PutLog("[MA]@@@@@ Disconnecting Gatt");
                mGatt.disconnect();
                mGatt.close();
            }

            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            } else {
                if(currentOS.SetBluetoothProperties(this.getApplicationContext(), false))
                    currentOS.SetScanLeProperties(true, mBluetoothAdapter, TapIn.this,this, false);
            }
            TapInOutTracking();
        } catch (Exception ex) {
            dbObj.PutLog("[@@MA]EX msaCheckInCheckout " + ex.toString());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        try {
            switch (requestCode) {
                case 1: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        dbObj.PutLog("[MA]coarse location permission granted");
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(getString(R.string.permissionDialogTitle));
                        builder.setMessage(getString(R.string.permissionDialogMessage));
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }

                        });
                        builder.show();
                    }
                }
            }
        }
        catch(Exception ex){
            dbObj.PutLog("[@@MA]EX onRequestPermissionsResult " + ex.toString());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
    }


    /**
     * Connect to GATT Server hosted by this device. Caller acts as GATT client.
     * The callback is used to deliver results to Caller, such as connection status as well
     * as any further GATT client operations.
     * The method returns a BluetoothGatt instance. You can use BluetoothGatt to conduct
     * GATT client operations.
     * @throws IllegalArgumentException if callback is null
     */
    public void connectToDevice(final BluetoothDevice device) {
        try {
            dbObj = new MSASQLiteDB(TapIn.this);
            if (processCompleted) {
                //processCompleted = true;
                Log.i("MSA", "***** STOP SCAN");
                // currentOS = AndroidVersionRepository.newInstance();
                if (mGatt != null) {
                    mGatt.disconnect();
                    mGatt.close();
                }

                //loadingDialog.progressDialog.dismiss();


                Log.i("MSA", "***** GO TO IN OUT ACTIVITY");
                Intent intent = new Intent(callBackContext,
                        ActivityMain.class);
                intent.putExtra("TapInType", "Bluetooth");
                intent.putExtra("mCheckInOutVal", mCheckInOutVal);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callBackContext.startActivity(intent);
                // currentOS.StopScan();
                // if (currentOS.SetBluetoothProperties(this.getApplicationContext(),true))
            }
            else{runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mGatt == null) {
                        dbObj.PutLog("[MA]connectToDevice Setting GATT VAR");
                        mGatt = device.connectGatt(TapIn.this, false, gattCallback);

                        currentOS = AndroidVersionRepository.newInstance();
                        currentOS.SetScanLeProperties(false, mBluetoothAdapter, TapIn.this, TapIn.this, false);

                    }
                }
            });}

        } catch (Exception ex) {
            if (dbObj != null) {
                dbObj.PutLog("[@@MA]connectToDevice  Exception" + ex.toString());
            }
            if (mGatt != null) {
                mGatt.disconnect();
                mGatt.close();
            }
        } finally {
            if (mGatt != null) {
                // mGatt.close();
                mGatt = null;
                loadingDialog.dismiss();
            }
        }
    }


    public void TapInOutTracking(){
        try {
            CommonUtils commonUtils = new CommonUtils(this.getApplicationContext());
            //Intent intent = getIntent();
            //Bundle bundle = intent.getBundleExtra("userRegistration");
            //String username = bundle.getString("userRegistration");
            //String tapValue = mCheckInOutVal ? "In" : "Out";
            //final inc.bitwise.vpnazure.Models.UserRegistration userRegistration = new inc.bitwise.vpnazure.Models.UserRegistration();

            SharedPreferences sharedPreferences = callBackContext.getSharedPreferences(SHARED_PREFS,callBackContext.MODE_PRIVATE);
            //text = sharedPreferences.getString(TEXT, "");
            String username = sharedPreferences.getString(Username, "");
            String tapValue = mCheckInOutVal ? "In" : "Out";

            Log.i("MSA", "@@@@@@@@@##############");
            commonUtils.TapIntrackingUpdate(username, tapValue);
        }
        catch(Exception ex)
        {
            dbObj.PutLog("[@@MA]EX TapInOutTracking" + ex.toString());
        }
    }


    //GATT callback handler that will receive asynchronous callbacks.
    public  final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        /** Get the Bluetooth profile state */
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            try {
                dbObj = new MSASQLiteDB(TapIn.this);
                dbObj.PutLog("[MA]BleGatt State-"+newState+" status-"+status);
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        gatt.discoverServices();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        break;
                    default:
                }
            }
            catch(Exception ex){
                dbObj.PutLog("[@@MA]EX BluetoothGattCallback" + ex.toString());
                if(gatt != null) {
                    gatt.disconnect();
                    gatt.close();
                }
            }

        }



        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            try {
                dbObj = new MSASQLiteDB(callBackContext);
                dbObj.PutLog("[MA]onServicesDiscovered");
                //Gets the active bluetooth services discovered from BLE Device
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService bg : services) {

                    Log.i(bg.toString(), bg.getUuid().toString());

                    if (UUID_MSA_APP.equals(bg.getUuid())) {
                        dbObj.PutLog("[MA]MSA APP found");
                        if(!bg.getCharacteristics().isEmpty() && bg.getCharacteristics().size() != 0) {
                            dbObj.PutLog("[MA]call read Characteristic");
                            BluetoothGattCharacteristic msacode = bg.getCharacteristics().get(0);
                            //Reads the requested characteristic from the associated remote device.
                            gatt.readCharacteristic(msacode);
                        }
                        else
                        {
                            dbObj.PutLog("bg.getCharacteristics()::"+bg.getCharacteristics().get(0));
                            dbObj.PutLog("[MA]bg size is 0");
                        }
                    }
                    else{
                        dbObj.PutLog("[MA]UUID_MSA_APP not Matching found");
                    }
                }
            }
            catch(Exception ex){
                dbObj.PutLog("[@@MA]EX onServicesDiscovered " + ex.toString());
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            try {

                dbObj = new MSASQLiteDB(callBackContext);
                //Reads the requested characteristic and get the characteristic value.
                final byte[] data = characteristic.getValue();

                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);

                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));

                    dbObj.PutLog("[MA]onCharacteristicRead" +  new String(data));

                    /*Intent intent = new Intent(callBackContext, InOutActivity.class);
                    intent.putExtra("UniqueCode",new String(data));
                    intent.putExtra("mCheckInOutVal", mCheckInOutVal);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    callBackContext.startActivity(intent);*/

                    //todo delete this
                    Log.i("MSA","data "+ new String(data));
                    //loadingDialog.progressDialog.dismiss();

                }
            }
            catch(Exception ex){
                dbObj.PutLog("[@@MA]EX onCharacteristicRead " + ex.toString());
            }
            finally{
                if(gatt != null){
                    gatt.disconnect();
                    gatt.close();
                }
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        try {
            Intent activateAdminPolicyIntent = utilityObj.ValidateDeviceAdminPolicyStatus();
            if (activateAdminPolicyIntent != null) {
                startActivityForResult(activateAdminPolicyIntent, REQUEST_ENABLE);
            }
            initialise(this);
            DisplayCameraStatus();
        }
        catch (Exception ex)
        {
            dbObj.PutLog("[@@MA]EX onResume "+ex.toString());
        }
    }

    /*@Override
    public void onRestart(){
        super.onRestart();
        finish();
        //startActivity(getIntent());
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            dbObj.PutLog("[MA]onActivityResult");

            if(mGatt != null)
            {dbObj.PutLog("[MA]@@@@ Closing MGATT on Activity result");
                mGatt.disconnect();
                mGatt.close();
            }
            loadingDialog.dismiss();
        } catch (Exception e) {
            dbObj.PutLog("[@@MA]EX onActivityResult "+e.toString());
        }

    }
}
