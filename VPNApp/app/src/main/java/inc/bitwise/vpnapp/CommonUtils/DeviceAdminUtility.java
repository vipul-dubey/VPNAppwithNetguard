package inc.bitwise.vpnapp.CommonUtils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import inc.bitwise.vpnapp.R;

public class DeviceAdminUtility {
    Context utilityContext;
    DevicePolicyManager bitwiseDevicePolicyManager;
    ComponentName bitwiseDevicePolicyAdmin;

    public DeviceAdminUtility(Context context){
        this.utilityContext = context;
        bitwiseDevicePolicyAdmin = new ComponentName(utilityContext,DevicePolicyAdmin.class);
        bitwiseDevicePolicyManager = (DevicePolicyManager) utilityContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    public Intent InstantiateDeviceAdminPolicyIntent() throws Exception {

        Intent devicePolicyManagerintent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        bitwiseDevicePolicyAdmin = new ComponentName(utilityContext.getApplicationContext(), DevicePolicyAdmin.class);
        devicePolicyManagerintent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, bitwiseDevicePolicyAdmin);
        devicePolicyManagerintent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, utilityContext.getString(R.string.admin_explanation));
        return devicePolicyManagerintent;
    }

    public void DisableCamera() {
        bitwiseDevicePolicyManager.setCameraDisabled(bitwiseDevicePolicyAdmin, true);

    }

    public void EnableCamera() {
        bitwiseDevicePolicyManager.setCameraDisabled(bitwiseDevicePolicyAdmin, false);
    }

    public Boolean GetCameraStatus() {
        return bitwiseDevicePolicyManager.getCameraDisabled(bitwiseDevicePolicyAdmin);
    }

    public Intent ValidateDeviceAdminPolicyStatus() throws Exception {
        Intent activateAdminPolicy = null;

        if (!bitwiseDevicePolicyManager.isAdminActive(bitwiseDevicePolicyAdmin))
            activateAdminPolicy = InstantiateDeviceAdminPolicyIntent();

        return activateAdminPolicy;
    }

    public Intent ValidateAndApplyPasswordPolicy() {
        Intent setPasswordIntent = null;

        bitwiseDevicePolicyManager.setMaximumTimeToLock(bitwiseDevicePolicyAdmin, 180000);
        bitwiseDevicePolicyManager.setPasswordQuality(bitwiseDevicePolicyAdmin, DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);

        boolean isSufficient = bitwiseDevicePolicyManager.isActivePasswordSufficient();
        if (!isSufficient) {
            setPasswordIntent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
            Long TIME_TO_EXPIRE_PASSWORD = Long.parseLong(utilityContext.getResources().getString(R.string.temp_password_expiration_time));

            bitwiseDevicePolicyManager.setPasswordExpirationTimeout(bitwiseDevicePolicyAdmin, TIME_TO_EXPIRE_PASSWORD);
        }
        return setPasswordIntent;
    }

    public void setPasswordExpirationTimeout() {
        bitwiseDevicePolicyManager.setPasswordExpirationTimeout(bitwiseDevicePolicyAdmin, Long.parseLong(utilityContext.getResources().getString(R.string.password_expiration_time)));
    }

    public long getPasswordExpiration() {
        return bitwiseDevicePolicyManager.getPasswordExpiration(bitwiseDevicePolicyAdmin);
    }
}
