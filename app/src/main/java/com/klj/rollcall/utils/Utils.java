package com.klj.rollcall.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.klj.rollcall.bean.StudentBean;
import com.klj.rollcall.bean.TeacherBean;
import com.klj.rollcall.listener.SingleChooseListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 常用方法工具类
 */
public class Utils {

    static int selectedPosition = 0;

    /**
     * 时间格式化
     *
     * @param timestampString
     * @return
     */
    public static String toTransferTime(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(timestamp));
        return date;
    }

    /**
     * 获取到年、月
     *
     * @param timestampString
     * @return
     */
    public static String getYearAndMonth(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new java.text.SimpleDateFormat("yyyy.MM").format(new java.util.Date(timestamp));
        return date;
    }

    /**
     * 获取到日、时、分
     *
     * @param timestampString
     * @return
     */
    public static String getDayAndHourAndMinute(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new java.text.SimpleDateFormat("dd日 HH:mm").format(new java.util.Date(timestamp));
        return date;
    }

    /**
     * 获取登录后的账号，密码和角色
     *
     * @param context
     * @return
     */
    public static Map<String, String> getUserIdAndPwd(Context context) {
        //同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
        SharedPreferences sharedPreferences = context.getSharedPreferences("login",
                Activity.MODE_PRIVATE);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        String userId = sharedPreferences.getString("userId", "");
        String userPwd = sharedPreferences.getString("userPwd", "");
        Map<String, String> map = new HashMap<>();
        map.put("userName", userId);
        map.put("password", userPwd);
        return map;
    }

    /**
     * 将登陆账号,密码和角色保存起来
     */
    public static void saveUserIdAndPwd(Context context, String userId, String userPwd) {
        //实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = context.getSharedPreferences("login",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //用putString的方法保存数据
        editor.putString("userName", userId);
        editor.putString("password", userPwd);
        //提交当前数据
        editor.commit();
    }

    /**
     * 显示提示
     *
     * @param context
     * @param msg
     */
    public static void showMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 将学生信息保存到sp文件中
     *
     * @param context
     * @param student
     */
    public static void saveStudntInfo(Context context, StudentBean student) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(ConstantUtil.STUDENT_FILE_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("sNo", student.getsNo());
        editor.putString("sName", student.getsName());
        editor.putString("sSex", student.getsSex());
        editor.putInt("sAge", student.getsAge());
        editor.putString("mName", student.getmName());
        editor.putInt("sGrade", student.getsGrade());
        editor.putString("dName", student.getdName());
        editor.putString("sPhone", student.getsPhone());
        editor.putString("sPortrait", student.getsPortrait());
        //提交当前数据
        editor.commit();
    }

    /**
     * 获取学生信息
     *
     * @param context
     * @return
     */
    public static StudentBean getStudentInfo(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(ConstantUtil.STUDENT_FILE_NAME,
                Activity.MODE_PRIVATE);
        StudentBean student = new StudentBean();
        student.setsNo(mySharedPreferences.getString("sNo", ""));
        student.setsName(mySharedPreferences.getString("sName", ""));
        student.setsSex(mySharedPreferences.getString("sSex", ""));
        student.setsAge(mySharedPreferences.getInt("sAge", 0));
        student.setmName(mySharedPreferences.getString("mName", ""));
        student.setsGrade(mySharedPreferences.getInt("sGrade", new Date().getYear()));
        student.setdName(mySharedPreferences.getString("dName", ""));
        student.setsPhone(mySharedPreferences.getString("sPhone", ""));
        student.setsPortrait(mySharedPreferences.getString("sPortrait", ""));
        //提交当前数据
        return student;
    }

    /**
     * 将教师信息保存到sp文件中
     *
     * @param context
     * @param teacher
     */
    public static void saveTeacherInfo(Context context, TeacherBean teacher) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(ConstantUtil.Teacher_FILE_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("tNo", teacher.gettNo());
        editor.putString("tName", teacher.gettName());
        editor.putString("tSex", teacher.gettSex());
        editor.putInt("tAge", teacher.gettAge());
        editor.putString("dName", teacher.getdName());
        editor.putString("tPhone", teacher.gettPhone());
        editor.putString("tPortrait", teacher.gettPortrait());
        //提交当前数据
        editor.commit();
    }

    /**
     * 获取教师信息
     *
     * @param context
     * @return
     */
    public static TeacherBean getTeacherInfo(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(ConstantUtil.Teacher_FILE_NAME,
                Activity.MODE_PRIVATE);
        TeacherBean teacher = new TeacherBean();
        teacher.settNo(mySharedPreferences.getString("tNo", ""));
        teacher.settName(mySharedPreferences.getString("tName", ""));
        teacher.settSex(mySharedPreferences.getString("tSex", ""));
        teacher.settAge(mySharedPreferences.getInt("tAge", 0));
        teacher.setdName(mySharedPreferences.getString("dName", ""));
        teacher.settPhone(mySharedPreferences.getString("tPhone", ""));
        teacher.settPortrait(mySharedPreferences.getString("tPortrait", ""));
        //提交当前数据
        return teacher;
    }

    /**
     * 保存信息到SP文件中
     *
     * @param context
     * @param fileName
     */
    public static void saveSPFile(Context context, String fileName, String key, Object value) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(fileName,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            editor.putString(key, value.toString());
        }
        //提交当前数据
        editor.commit();
    }

    /**
     * 获取保存的SP文件
     *
     * @param context
     * @param fileName
     * @param key
     * @param defaultValue
     * @return
     */
    public static Object getSPFile(Context context, String fileName, String key, Object defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName,
                Activity.MODE_PRIVATE);
        if (defaultValue instanceof String) {
            return sharedPreferences.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultValue);
        }
        return null;
    }

    public static void singleChooseAlertDialog(Context cxt, String title, String[] source, int position, final SingleChooseListener listener) {
        selectedPosition = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
        builder.setTitle(title)
                .setSingleChoiceItems(source, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedPosition = i;
                    }
                })
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onChoose(selectedPosition);
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create().show();
    }

    public static void signinAlertDialog(Context cxt, String title, final String[] source, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cxt);
        builder.setTitle(title)
                .setAdapter(new ArrayAdapter<String>(cxt, android.R.layout.simple_list_item_1, source), listener)
//                .setNegativeButton("确定", listener)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create().show();
    }
}
