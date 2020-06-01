package inc.bitwise.vpnapp.CommonUtils.LoadingDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Vipuld on 17/09/2019.
 */
public class LoadingDialog
{
    public static ProgressDialog progressDialog;

    public static void show(Activity activity, String message)
    {
        if (activity == null || activity.isFinishing())
        {
            return;
        }
        dismiss();
        hideSoftKeyboard(activity);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setOwnerActivity(activity);
        progressDialog.show();
    }

    public static void dismiss()
    {
        try
        {
            if (progressDialog != null)
            {
                Activity activity = progressDialog.getOwnerActivity();
                if (activity != null && !activity.isFinishing())
                {
                    progressDialog.dismiss();
                }
                progressDialog = null;
            }
        } catch (Exception e)
        {
            Log.i("MSA","dismissDialogue "+e.toString());
        }
    }

    public static void hideSoftKeyboard(Activity activity)
    {
        try
        {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e)
        {
        }
    }

}
