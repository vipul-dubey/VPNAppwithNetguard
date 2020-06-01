package inc.bitwise.vpnapp.CommonUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TransitionManager {
    public static void startActivity(Activity activity, Class toActivity)
    {
        startActivity(activity, toActivity, null);
    }

    public static void startActivity(Activity activity, Intent intent)
    {
        activity.startActivity(intent);
    }

    public static void startActivity(Activity activity, Class toActivity, Bundle bundle)
    {
        Intent i = new Intent(activity, toActivity);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (bundle != null)
        {
            i.putExtras(bundle);
        }
        activity.startActivity(i);
    }

    public static void startActivityForResult(Activity activity, Class toActivity, Bundle bundle, int requestCode)
    {
        Intent i = new Intent(activity, toActivity);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (bundle != null)
        {
            i.putExtras(bundle);
        }
        activity.startActivityForResult(i, requestCode);
    }

    public static void startActivityForResultNewInstance(Activity activity, Class toActivity, Bundle bundle, int requestCode)
    {
        Intent i = new Intent(activity, toActivity);
        if (bundle != null)
        {
            i.putExtras(bundle);
        }
        activity.startActivityForResult(i, requestCode);
    }

    public static void startActivityInNewTask(Activity activity, Class toActivity)
    {
        startActivityInNewTask(activity, toActivity, null, true);
    }

    public static void startActivityInNewTask(Activity activity, Class toActivity, Bundle bundle, boolean withoutAnimation)
    {
        Intent i = new Intent(activity, toActivity);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (bundle != null)
        {
            i.putExtras(bundle);
        }
        activity.startActivity(i);
        if(withoutAnimation)
        {
            activity.overridePendingTransition(0, 0);
        }
    }

    public static Bundle getBundle(Activity activity)
    {
        Intent intent = activity.getIntent();
        Bundle bundle = null;
        if (intent != null)
        {
            bundle = intent.getExtras();
        }

        if (bundle == null)
        {
            bundle = new Bundle();
        }

        return bundle;
    }
}
