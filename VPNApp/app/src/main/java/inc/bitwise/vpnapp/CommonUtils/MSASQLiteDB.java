package inc.bitwise.vpnapp.CommonUtils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import inc.bitwise.vpnapp.R;


public class MSASQLiteDB extends SQLiteOpenHelper
{
    Context sqliteDBContext;
    private static final String DATABASE_NAME = "BitwiseSecurity";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_Device_Status = "Device";
    private static final String TABLE_AlertCounter_Status = "AlertCounter";
    private static final String TABLE_User_Status ="User";
    private static final String TABLE_Admin_Status = "Admin";
    private static final String TABLE_Logger_Status = "Logger";
    private static final String TABLE_UserImg_Status = "UserImage";
    private static final String Logger_LogDate = "LogDate";
    private static final String Register_Token_Info = "RegTokenInfo";
    private static final String SocialLoginRegisterTokenInfo = "SocialLoginRegisterTokenInfo";
    private static final String Table_Airplane_Mode_Status = "AirplaneModeStatus";

    public MSASQLiteDB(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqliteDBContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            String USER_TABLE = sqliteDBContext.getString(R.string.CreateUserTable);
            db.execSQL(USER_TABLE);

            String DEVICE_TABLE = sqliteDBContext.getString(R.string.CreateDeviceTable);
            db.execSQL(DEVICE_TABLE);

            String ADMIN_TABLE = sqliteDBContext.getString(R.string.CreateAdminTable);
            db.execSQL(ADMIN_TABLE);

            String LOGGER_TABLE = sqliteDBContext.getString(R.string.CreateLoggerTable);
            db.execSQL(LOGGER_TABLE);

            String USERIMG_TABLE = sqliteDBContext.getString(R.string.CreateUserImageTable);
            db.execSQL(USERIMG_TABLE);

            String RegToken_TABLE = sqliteDBContext.getString(R.string.CreateRegTokenInfoTable);
            db.execSQL(RegToken_TABLE);

            String SocialLoginRegToken_TABLE = sqliteDBContext.getString(R.string.CreateSocialLoginRegTokenInfoTable);
            db.execSQL(SocialLoginRegToken_TABLE);

            String AirplaneModeStatus_TABLE = sqliteDBContext.getString(R.string.CreateAirplaneModeStatus);
            db.execSQL(AirplaneModeStatus_TABLE);

        }
        catch(Exception e)
        {
            PutLog("[@@DB]EX onCreate "+e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Admin_Status);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_AlertCounter_Status);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Device_Status);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Logger_Status);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_User_Status);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_UserImg_Status);
            db.execSQL("DROP TABLE IF EXISTS " + Register_Token_Info);
            db.execSQL("DROP TABLE IF EXISTS " + Table_Airplane_Mode_Status);
            db.execSQL("DROP TABLE IF EXISTS " + SocialLoginRegisterTokenInfo);
            onCreate(db);
        }
    }

    private static boolean objClosed = true;

    public void PutLog(String log) {

        SQLiteDatabase dbPutLog =  null;

        try {
            if(objClosed)
            {
                dbPutLog = this.getWritableDatabase();
                objClosed = false;
            }

            ContentValues values = new ContentValues();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            SimpleDateFormat LogDateFormat = new SimpleDateFormat("yyyyMMdd");

            values.put("Log", log + "-" + dateFormat.format(date));
            values.put("LogDate", LogDateFormat.format(date));
            // Inserting Row
            dbPutLog.insert(TABLE_Logger_Status, null, values);
        }
        catch (Exception e) {

        }
        finally {
            if (dbPutLog != null && dbPutLog.isOpen()) {
                dbPutLog.close();
                objClosed = true;
            }
        }
    }

    public String ReadLogs(Context ctx)
    {
        Cursor cursor = null;
        String strLogs="";
        SQLiteDatabase db= null;
        try
        {
            String selectQuery = sqliteDBContext.getString(R.string.GetLog);
            db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    strLogs += cursor.getString(0)+ "\n";
                }
                while (cursor.moveToNext());
            }
        }
        catch(Exception e)
        {
            PutLog("[@@DB]EX ReadLogs "+e.getMessage());
        }

        finally {
            if(cursor!=null) {
                cursor.close();
            }
            if(db!= null && db.isOpen()) {
                db.close();
            }
        }
        return strLogs;
    }


    public void ClearLogsFromDevice(String previousDate) {
        SQLiteDatabase db = null;
        try{
            db = this.getWritableDatabase();
            db.delete(TABLE_Logger_Status, Logger_LogDate + "<?", new String[]{previousDate});
        }
        catch(Exception e){PutLog("[@@DB]ClearLogs " + e);}
        finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
    // Table Admin
    public void AddAdminTableInfo(String violationMailSent)
    {
        SQLiteDatabase db = null;
        try
        {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(sqliteDBContext.getString(R.string.Admin_ViolationEmailSent), violationMailSent);
            db.insert(TABLE_Admin_Status, null, values);
        }
        catch(Exception e)
        {
            PutLog("[@@DB]EX AddAdminTableInfo " + e.getMessage());
        }

        finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // Set-Get Violation Email sent status
    public void setViolationEmailSent(String emailSentValue)
    {
        SQLiteDatabase db = null;
        try
        {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(sqliteDBContext.getString(R.string.Admin_ViolationEmailSent), emailSentValue);
            db.update(TABLE_Admin_Status,values, null,null );
        }
        catch(Exception e){
            PutLog("[@@DB]EX setViolationEmailSent " + e.getMessage());
        }
        finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
    public String getViolationEmailSent()
    {
        SQLiteDatabase db = null;
        String Status = null;
        Cursor cursor = null;
        try{
            String selectQuery = sqliteDBContext.getString(R.string.GetViolationMailSentValue);
            db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null) {
                cursor.moveToFirst();
                Status =   cursor.getString(0).toString();
            }
        }
        catch(Exception e){
            PutLog("[@@DB]EX getViolationEmailSent " + e.getMessage());
        }
        finally {
            if(cursor!=null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return Status;
    }

    public ArrayList<String> getAdminInfo() {
        SQLiteDatabase db = null;
        ArrayList<String> UserData=null;
        Cursor cursor = null;
        try
        {
            String selectQuery = sqliteDBContext.getString(R.string.GetAdminInfo);

            db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            UserData = new ArrayList<String>();
            if (cursor.moveToFirst()) {
                do {
                    UserData.add(cursor.getString(0));
                    UserData.add(cursor.getString(1));
                    UserData.add(cursor.getString(2));
                    UserData.add(cursor.getString(3));
                    UserData.add(cursor.getString(4));
                } while (cursor.moveToNext());
            }

        }
        catch(Exception e){
            PutLog("[@@DB]EX getAdminInfo " + e.getMessage());
        }
        finally {
            if(cursor!=null) {
                cursor.close();
            }
            if(db!=null && db.isOpen()) {
                db.close();
            }
        }
        return UserData;
    }
}