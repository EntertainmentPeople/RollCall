package com.klj.rollcall.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SPUtils {

    /** debug 环境下允许修改 sp文件的路径 */
    public static final boolean isDebug = true;
    /** 修改以后的sp文件的路径 MyApplication.getContext().getExternalFilesDir(null).getAbsolutePath()=/sdcard/Android/%package_name%/file */
//    public static final String FILE_PATH = MyApplication.getContext().getExternalFilesDir(null).getAbsolutePath();

    /**
     * 保存数据
     *
     * @param context
     * @param fileName 文件名, 不需要".xml"
     * @param keyName
     * @param value
     */
    public static void saveSPFile(Context context, String fileName, String keyName, Object value) {
        SharedPreferences.Editor editor = getSharedPreferences(context, fileName).edit();
        if (value instanceof String) {
            editor.putString(keyName, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(keyName, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(keyName, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(keyName, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(keyName, (Long) value);
        } else {
            editor.putString(keyName, value.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 获取数据
     *
     * @param context
     * @param fileName
     * @param keyName
     * @param defaultValue 默认值
     * @return
     */
    public static Object getSPFile(Context context, String fileName, String keyName, Object defaultValue) {
        SharedPreferences sp = getSharedPreferences(context, fileName);
        if (defaultValue instanceof String) {
            return sp.getString(keyName, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return sp.getInt(keyName, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return sp.getBoolean(keyName, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            return sp.getFloat(keyName, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            return sp.getLong(keyName, (Long) defaultValue);
        }
        return null;
    }


    /**
     * 移除某个key值对应的值
     *
     * @param context
     * @param fileName
     * @param keyName
     */
    public static void remove(Context context, String fileName, String keyName) {
        SharedPreferences.Editor editor = getSharedPreferences(context, fileName).edit();
        editor.remove(keyName);
        SharedPreferencesCompat.apply(editor);
    }

    /** 清除所有数据 */
    public static void clear(Context context, String fileName) {
        SharedPreferences.Editor editor = getSharedPreferences(context, fileName).edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param keyName
     * @return
     */
    public static boolean contains(Context context, String fileName, String keyName) {
        SharedPreferences sharedPreferences = getSharedPreferences(context, fileName);
        return getSharedPreferences(context, fileName).contains(keyName);
    }

    /** 返回所有的键值对 */
    public static Map<String, ?> getAll(Context context, String fileName) {
        return getSharedPreferences(context, fileName).getAll();
    }


    /** 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类 */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /** 反射查找apply的方法 */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /** 如果找到则使用apply执行，否则使用commit */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

    /**
     * @param context
     * @param fileName
     */
    private static SharedPreferences getSharedPreferences(Context context, String fileName) {
        // 返回默认路径下的 SharedPreferences : /data/data/%package_name%/shared_prefs/%fileName%.xml
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

}