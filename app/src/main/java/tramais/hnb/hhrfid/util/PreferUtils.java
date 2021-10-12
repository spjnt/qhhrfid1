package tramais.hnb.hhrfid.util;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferUtils {

    // 设置String的内容
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value).commit();
    }

    // 获取String的内容
    public static String getString(Context context, String key, String defvalue) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key, defvalue);
    }

    // 获取String的内容
    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }

    // boolean:设置
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    // 获取boolean的值
    public static boolean getBoolean(Context context, String key, boolean defvalue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defvalue);
    }

    // 获取boolean的值
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }


    // int
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key, int defvalue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getInt(key, defvalue);
    }


    // int
    public static void putLong(Context context, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        sp.edit().putLong(key, value).commit();
    }

    public static long getLong(Context context, String key, long defvalue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getLong(key, defvalue);
    }

    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

}
