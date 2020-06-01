package inc.bitwise.vpnapp.CommonUtils.LoadingDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class UIAlertDialog {
    public static AlertDialog showAlert(Activity context, String message, String title, DialogInterface.OnClickListener listener, boolean isCancelable)
    {
        return showAlert(context, message, title, "Okay", listener, isCancelable);
    }

    public static AlertDialog showAlert(Activity context, String message, String title, String buttonText, DialogInterface.OnClickListener listener, boolean isCancelable)
    {
        if (context == null || context.isFinishing())
        {
            return null;
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        if (title != null)
        {
            alertDialog.setTitle(title);
        }
        alertDialog.setMessage(message).setPositiveButton(buttonText, listener).setCancelable(isCancelable);
        AlertDialog dialog = alertDialog.show();
        return dialog;
    }

    public static AlertDialog showAlert(Activity context, String message)
    {
        return showAlert(context, message, null);
    }

    public static AlertDialog showAlert(Activity context, String message, String title)
    {
        return showAlert(context, message, title, null, true);
    }

    public static AlertDialog showAlert(Activity context, String message, String title, String positiveButtonText, DialogInterface.OnClickListener positiveListener, String negativeButtonText, final DialogInterface.OnClickListener negativeListener, boolean isCancelable)
    {
        if (context == null || context.isFinishing())
        {
            return null;
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        if (title != null)
        {
            alertDialog.setTitle(title);
        }
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(positiveButtonText, positiveListener);
        alertDialog.setNegativeButton(negativeButtonText, negativeListener);
        alertDialog.setCancelable(isCancelable);
        if(isCancelable && negativeListener != null)
        {
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    negativeListener.onClick(dialog, AlertDialog.BUTTON_NEGATIVE);
                }
            });
        }
        AlertDialog dialog = alertDialog.show();

        return dialog;
    }
}
