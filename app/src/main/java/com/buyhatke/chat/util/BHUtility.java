package com.buyhatke.chat.util;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.buyhatke.chat.R;

import org.slf4j.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class BHUtility {

    final DateFormat TIME = new SimpleDateFormat("H:mm");

    /**
     * Method to display toast
     *
     * @param context context of activity
     * @param message message to be displayed
     */
    public static void showToast(Context context, String message, int length) {
        Toast.makeText(context, message, length).show();
    }

    /**
     * Method to write data to shared prefernces
     *
     * @param context context of activity
     * @param key     key for shared preferences
     * @param value   value to be written
     */
    public static void writeBooleanToPreferences(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BHConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void writeStringToPreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BHConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringFromPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BHConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, BHConstants.SHARED_PREFERENCE_DEF_VALUE);
    }

    /**
     * Method to read data from shared preferences
     *
     * @param context context of activity
     * @param key     key for shared preferences
     * @return value for the key or default value
     */
    public static boolean getBooleanFromPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BHConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, BHConstants.SHARED_PREFERENCE_DEF_BOOLEAN_VALUE);
    }

    public static boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Encrypt the clear-text password using the same salt that was used to
        // encrypt the original password
        byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);

        // Authentication succeeds if encrypted password that the user entered
        // is equal to the stored hash
        return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
    }

    public static byte[] getEncryptedPassword(String password, byte[] salt) {
        // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
        // specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
        String algorithm = "PBKDF2WithHmacSHA1";
        byte[] encryptedPwd = null;
        // SHA-1 generates 160 bit hashes, so that's what makes sense here
        int derivedKeyLength = 160;

        int iterations = 20000;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);

        SecretKeyFactory f = null;
        try {
            f = SecretKeyFactory.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            encryptedPwd = f.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return encryptedPwd;
    }

    public static byte[] generateSalt() {

        // VERY important to use SecureRandom instead of just Random
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        return salt;
    }

    public static Logger getLogger(String className) {
        return LoggerFactory.getLogger(className);
    }

    public static AlertDialog getErrorDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton("OKAY", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setTitle("Error");
        return builder.create();
    }

    public static void createAndSendNotification(int notifyId, String title, String body, Context context, PendingIntent pendingIntent) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_account).setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(body).setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notificationCompat = mBuilder.build();
        notificationCompat.defaults |= Notification.DEFAULT_SOUND;
        notificationCompat.defaults |= Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(notifyId, notificationCompat);

    }

    public static boolean isRegistered(Context context) {
        return getBooleanFromPreferences(context, BHConstants.SHARED_PREFERENCE_KEY_REG);
    }

    public static void storeUser(Context context,String username) {
        writeStringToPreferences(context, BHConstants.SHARED_PREFERENCE_KEY_USER_NAME,username);
        writeStringToPreferences(context, BHConstants.SHARED_PREFERENCE_KEY_JID,username);//+"@"+ ServerConfiguration.HOST);
    }

    public static String getUsername(){
        return getStringFromPreferences(BHApplication.getInstance(),
                BHConstants.SHARED_PREFERENCE_KEY_USER_NAME);
    }

    public static String getJID(){
        return getStringFromPreferences(BHApplication.getInstance(),
                BHConstants.SHARED_PREFERENCE_KEY_JID);
    }

    public static String getSmartTimeText(Context context, Date timeStamp) {
        if (timeStamp == null) {
            return "";
        }
        final DateFormat TIME = new SimpleDateFormat("H:mm");
        // today
        Calendar midnight = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);

        if (timeStamp.getTime() > midnight.getTimeInMillis()) {
            synchronized (TIME) {
                return timeFormat.format(timeStamp);
            }
        } else {
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
            return dateFormat.format(timeStamp) + " " + timeFormat.format(timeStamp);
        }
    }


    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
