package com.klj.rollcall.bean;

/**
 * Created by 娱乐人物 on 2017/5/15.
 */

public class AttendanceBean extends BaseBean {
    private String sNo;             //学号
    private String sName;           //姓名
    private String sSex;           //性别
    private String sPortrait;
    private String sDname;           //院系
    private String sMname;           //专业
    private int sGrade;           //年级
    private long aTime;           //考勤时间
    private String aType;           //考勤类型

    public AttendanceBean() {
    }

    public AttendanceBean(String sNo, String sName, String sSex, String sPortrait, String sDname, String sMname, int sGrade) {
        this.sNo = sNo;
        this.sName = sName;
        this.sSex = sSex;
        this.sPortrait = sPortrait;
        this.sDname = sDname;
        this.sMname = sMname;
        this.sGrade = sGrade;
    }

    public AttendanceBean(String sNo, String sName, String sSex, String sPortrait, String sDname, String sMname, int sGrade, long aTime, String aType) {
        this.sNo = sNo;
        this.sName = sName;
        this.sSex = sSex;
        this.sPortrait = sPortrait;
        this.sDname = sDname;
        this.sMname = sMname;
        this.sGrade = sGrade;
        this.aTime = aTime;
        this.aType = aType;
    }

    public String getsPortrait() {
        return sPortrait;
    }

    public void setsPortrait(String sPortrait) {
        this.sPortrait = sPortrait;
    }

    public String getsNo() {
        return sNo;
    }

    public void setsNo(String sNo) {
        this.sNo = sNo;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsSex() {
        return sSex;
    }

    public void setsSex(String sSex) {
        this.sSex = sSex;
    }

    public String getsDname() {
        return sDname;
    }

    public void setsDname(String sDname) {
        this.sDname = sDname;
    }

    public String getsMname() {
        return sMname;
    }

    public void setsMname(String sMname) {
        this.sMname = sMname;
    }

    public int getsGrade() {
        return sGrade;
    }

    public void setsGrade(int sGrade) {
        this.sGrade = sGrade;
    }

    public long getaTime() {
        return aTime;
    }

    public void setaTime(long aTime) {
        this.aTime = aTime;
    }

    public String getaType() {
        return aType;
    }

    public void setaType(String aType) {
        this.aType = aType;
    }

    @Override
    public String toString() {
        return "AttendanceBean{" +
                "sNo='" + sNo + '\'' +
                ", sName='" + sName + '\'' +
                ", sSex='" + sSex + '\'' +
                ", sPortrait='" + sPortrait + '\'' +
                ", sDname='" + sDname + '\'' +
                ", sMname='" + sMname + '\'' +
                ", sGrade='" + sGrade + '\'' +
                ", aTime=" + aTime +
                ", aType=" + aType +
                '}';
    }
}
