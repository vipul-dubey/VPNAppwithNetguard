package inc.bitwise.vpnapp.Registration;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.bitwise.vpnapp.BuildConfig;
import inc.bitwise.vpnapp.CommonUtils.LoadingDialog.LoadingDialog;
import inc.bitwise.vpnapp.CommonUtils.Model.UserSessionManager;
import inc.bitwise.vpnapp.CommonUtils.TransitionManager;
import inc.bitwise.vpnapp.R;
import inc.bitwise.vpnapp.TapInProcess.TapIn;
import inc.bitwise.vpnazure.Services.RegistrationService;
import inc.bitwise.vpnazure.Services.Service;
import inc.bitwise.vpnazure.Utility.AsyncLoader;
import inc.bitwise.vpnazure.Utility.FormValidator;

/**
 * Created by Vipuld on 17/09/2019.
 */
public class UserRegistration extends FragmentActivity {
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.aadharId)
    TextView aadharId;
    @BindView(R.id.mobileNo)
    TextView mobileNo;
    @BindView(R.id.department)
    Spinner departmentName;

    @BindView(R.id.register)
    Button register;

    UserSessionManager session;
    boolean isUserLoggedIn = false;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String Username ="Username";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userregistration);
        ButterKnife.bind(this);
        try{
            initialise(this);
            isUserLoggedIn = session.isUserLoggedIn();

            if(isUserLoggedIn)
            {
                TransitionManager.startActivity(UserRegistration.this,TapIn.class);
                UserRegistration.this.finish();
            }
        } catch (Exception e){
            Log.i("URException ","@@@@###### "+e.toString());
        }

    }
    private void initialise(Context context){
        try {
            Resources resources = context.getResources();
            String azureHost = resources.getString(R.string.AzureHost);
            Service.initialize(context, azureHost, BuildConfig.DEBUG);
            session = new UserSessionManager(context);
        }
        catch (Exception ex) {
            Log.i("URException ","@@@@###### "+ex.toString());
        }
    }

    @OnClick(R.id.register)
    protected void registerUser()
    {
        try {
            final inc.bitwise.vpnazure.Models.UserRegistration userRegistration = new inc.bitwise.vpnazure.Models.UserRegistration();
            if (validateFields()) {
                userRegistration.setUserName(username.getText().toString());
                userRegistration.setAadharId(aadharId.getText().toString());
                userRegistration.setMobileNo(mobileNo.getText().toString());
                userRegistration.setDepartmentName(departmentName.getSelectedItem().toString());


                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(Username, username.getText().toString());
                editor.commit();

                LoadingDialog.show(this, getString(R.string.RegisterDialogMessage));
                try {

                    AsyncLoader.load(new AsyncLoader.SyncBlock() {
                        @Override
                        public void run() throws Exception {

                            JSONObject isregistered = RegistrationService.sharedInstance().registerUser(userRegistration);
                            String isSuccess = isregistered.getString("isSuccess");

                                AsyncLoader.loadOnUIThread(new AsyncLoader.CompletionBlock() {
                                    @Override
                                    public void run(Exception exception) {
                                        if (isSuccess.equalsIgnoreCase("true")) {
                                        LoadingDialog.dismiss();
                                        session.createUserLoginSession(userRegistration);
                                        Toast.makeText(UserRegistration.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("userRegistration", userRegistration.userName);
                                        TransitionManager.startActivity(UserRegistration.this, TapIn.class,bundle);
                                        finish();
                                        }
                                        else{
                                            LoadingDialog.dismiss();
                                            try {
                                                Toast.makeText(UserRegistration.this, isregistered.getJSONArray("errorMessage").getJSONObject(0).getString("value").toString(), Toast.LENGTH_LONG).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                        }
                    }, new AsyncLoader.CompletionBlock() {
                        @Override
                        public void run(Exception exception) {
                            LoadingDialog.dismiss();
                            Toast.makeText(UserRegistration.this, "Network Error " + exception.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Log.i("MSA", "isregisteredException " + e.toString());
                    e.printStackTrace();
                    Toast.makeText(UserRegistration.this, "Network Error " + e.toString(), Toast.LENGTH_LONG).show();
                }
            } else
                Toast.makeText(UserRegistration.this, "Enter Correct Details", Toast.LENGTH_SHORT).show();


        }
        catch (Exception e)
        {
            Log.i("URException ","@@@@###### "+e.toString());
            Toast.makeText(UserRegistration.this, "Network Error " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    protected boolean validateFields()
    {
        boolean validMobileNumber = validateMobileNumber();
        boolean validAadhar = validateAadharId();
        boolean validUserName = validateUserName();
        boolean validDepartmentName = validateDepartmentName();
        boolean areAllFieldsValid = validUserName && validAadhar && validMobileNumber && validDepartmentName;
        return areAllFieldsValid;
    }

    private boolean validateUserName() {
        String userName = username.getText().toString();
        boolean validName = FormValidator.isValidName(userName);
        if (!validName)
        {
            username.setError(getString(R.string.validateNeedUsername));
            username.requestFocus();
        }
        return validName;
    }

    private boolean validateDepartmentName() {
        String _departmentName = departmentName.getSelectedItem().toString();
        //boolean validDepartmentName = FormValidator.isValidName(_departmentName);
        if (_departmentName.contains("Choose Department"))
        {
            departmentName.setPrompt(getString(R.string.validateNeedUsername));
            Toast.makeText(UserRegistration.this, "Please select Department", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        return true;
    }


    private boolean validateAadharId() {
        String aadharid = aadharId.getText().toString();
        boolean validAadhar = FormValidator.isValidAadharId(aadharid,12);
        if (!validAadhar)
        {
            aadharId.setError(getString(R.string.validateAadharId));
            aadharId.requestFocus();
        }
        return validAadhar;
    }

    private boolean validateMobileNumber() {
        String mobileNumber = this.mobileNo.getText().toString();
        boolean validMobileNumber = mobileNumber .isEmpty() ? true : FormValidator.isValidCellPhone(mobileNumber , 10);
        if (!validMobileNumber )
        {
            this.mobileNo.setError(getString(R.string.validateNeedPhone));
            this.mobileNo.requestFocus();
        }
        return validMobileNumber;
    }
}
