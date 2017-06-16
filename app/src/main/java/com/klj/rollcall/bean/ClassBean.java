package com.klj.rollcall.bean;

/**
 * Created by 娱乐人物 on 2017/5/14.
 */

public class ClassBean extends BaseBean {
    private String className;
    private int studentNum;
    private String tName;
    private int start;
    private int weekday;

    public ClassBean() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(int studentNum) {
        this.studentNum = studentNum;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    @Override
    public String toString() {
        return "ClassBean{" +
                "className='" + className + '\'' +
                ", studentNum=" + studentNum +
                '}';
    }
}
