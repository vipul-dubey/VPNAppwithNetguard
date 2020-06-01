package inc.bitwise.vpnapp.CommonUtils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.ArrayList;

public class SecuritySMS {
    private Context contextForSMS;

    public SecuritySMS(Context context)
    {
        contextForSMS = context;
    }

    private String SMS_SENT = "SMS Sent to Security officer.";
    private String SMS_DELIVERED = "SMS is delivered by Security officer.";
    private int MAX_SMS_MESSAGE_LENGTH = 160;
    private int SMS_PORT = 2948;

    public boolean sendSms(String phonenumber,String message, boolean isBinary)
    {
        try
        {
            SmsManager manager = SmsManager.getDefault();

            PendingIntent piSend = PendingIntent.getBroadcast(contextForSMS, 0, new Intent(SMS_SENT), 0);
            PendingIntent piDelivered = PendingIntent.getBroadcast(contextForSMS, 0, new Intent(SMS_DELIVERED), 0);

            if (isBinary) {
                byte[] data = new byte[message.length()];

                for (int index = 0; index < message.length() && index < MAX_SMS_MESSAGE_LENGTH; ++index) {
                    data[index] = (byte) message.charAt(index);
                }

                manager.sendDataMessage(phonenumber, null, (short) SMS_PORT, data, piSend, piDelivered);
                return  true;
            } else {
                int length = message.length();

                if (length > MAX_SMS_MESSAGE_LENGTH) {
                    ArrayList<String> messagelist = manager.divideMessage(message);

                    manager.sendMultipartTextMessage(phonenumber, null, messagelist, null, null);
                } else {
                    manager.sendTextMessage(phonenumber, null, message, piSend, piDelivered);
                }

                return  true;
            }
        }
        catch (Exception ex)
        {}

        return  false;

    }
}
