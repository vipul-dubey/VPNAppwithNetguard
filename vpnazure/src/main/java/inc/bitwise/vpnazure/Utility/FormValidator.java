package inc.bitwise.vpnazure.Utility;

import android.text.TextUtils;

import java.util.Calendar;
import java.util.regex.Pattern;

public class FormValidator {

    private static final Pattern hasUppercase = Pattern.compile("[A-Z]");
    private static final Pattern hasLowercase = Pattern.compile("[a-z]");
    private static final Pattern hasNumber = Pattern.compile("\\d");
    private static final Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");

    public final static boolean isValidAadharId(String number, int numberLength)
    {
        number =number.replaceAll("[^\\d]+", "");
        number = removeWhiteSpaces(number);
        return !TextUtils.isEmpty(number) && android.util.Patterns.PHONE.matcher(number).matches() && (number.trim().length() == numberLength);

    }

    public static boolean isValidCellPhone(String number, int numberLength)
    {
        // Remove all characters other than digit.
        number =number.replaceAll("[^\\d]+", "");
        number = removeWhiteSpaces(number);
        return !TextUtils.isEmpty(number) && android.util.Patterns.PHONE.matcher(number).matches() && (number.trim().length() == numberLength);
    }

    public static boolean isValidZip(String zip, int zipLength)
    {
        zip = removeWhiteSpaces(zip);
        return !TextUtils.isEmpty(zip) && zip.trim().length() == zipLength;
    }

    public static boolean isValidPassword(String password, int passwordMinLength, int passwordMaxLength)
    {
        password = removeWhiteSpaces(password);
        return !TextUtils.isEmpty(password) && password.trim().length() >= passwordMinLength && password.trim().length() <= passwordMaxLength;
    }

    public static boolean isValidBirthday(int year, int day, int month, int minAge)
    {
        return isGreaterThanMinAge(year, day, month, minAge);
    }

    public static String removeWhiteSpaces(String string)
    {
        if (string == null)
        {
            return "";
        }
        return string.replaceAll("\\s+", "");
    }


    public static boolean isValidName(String string)
    {
        return string.matches(".*\\w.*");
    }

    public static boolean isNonEmptyString(String string)
    {
        return string != null && string.trim().length() != 0;
    }

    private static boolean isGreaterThanMinAge(int year, int month, int day, int minAge)
    {
        int age = getCalculatedAge (year, month, day);
        if (age < 0)
            return false;
        return age >= minAge;
    }

    public static boolean isSmallerThanMaxAllowedAge(int year, int month, int day, int maxAge)
    {
        int age = getCalculatedAge (year, month, day);
        if (age < 0)
            return false;
        return age < maxAge;
    }

    private static int getCalculatedAge (int year, int month, int day)
    {
        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);
        if (dob.after(now))
        {
            return -1;
        }
        int year1 = now.get(Calendar.YEAR);
        int year2 = dob.get(Calendar.YEAR);
        int age = year1 - year2;
        int month1 = now.get(Calendar.MONTH);
        int month2 = dob.get(Calendar.MONTH);
        if (month2 > month1)
        {
            age--;
        }
        else if (month1 == month2)
        {
            int day1 = now.get(Calendar.DAY_OF_MONTH);
            int day2 = dob.get(Calendar.DAY_OF_MONTH);
            if (day2 > day1)
            {
                age--;
            }
        }
        return age;
    }

}
