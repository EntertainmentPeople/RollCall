package com.klj.rollcall.bean;


/**
 * Created by 娱乐人物 on 2017/5/27.
 */

public class AttendanceType extends BaseBean{

    private int aTotal;
    private int aNormal;
    private int aLate;
    private int aLeaveEarly;
    private int aLeave;
    private int aAssentisson;
    private AttendanceBean attendanceBean;

    public AttendanceType() {
    }

    public AttendanceType(int aTotal,int aNormal, int aLate, int aLeaveEarly, int aLeave, int aAssentisson, AttendanceBean attendanceBean) {
        this.aTotal = aTotal;
        this.aNormal = aNormal;
        this.aLate = aLate;
        this.aLeaveEarly = aLeaveEarly;
        this.aLeave = aLeave;
        this.aAssentisson = aAssentisson;
        this.attendanceBean = attendanceBean;
    }

    public int getaTotal() {
        return aTotal;
    }

    public void setaTotal(int aTotal) {
        this.aTotal = aTotal;
    }

    public int getaNormal() {
        return aNormal;
    }

    public void setaNormal(int aNormal) {
        this.aNormal = aNormal;
    }

    public int getaLate() {
        return aLate;
    }

    public void setaLate(int aLate) {
        this.aLate = aLate;
    }

    public int getaLeaveEarly() {
        return aLeaveEarly;
    }

    public void setaLeaveEarly(int aLeaveEarly) {
        this.aLeaveEarly = aLeaveEarly;
    }

    public int getaLeave() {
        return aLeave;
    }

    public void setaLeave(int aLeave) {
        this.aLeave = aLeave;
    }

    public int getaAssentisson() {
        return aAssentisson;
    }

    public void setaAssentisson(int aAssentisson) {
        this.aAssentisson = aAssentisson;
    }

    public AttendanceBean getAttendanceBean() {
        return attendanceBean;
    }

    public void setAttendanceBean(AttendanceBean attendanceBean) {
        this.attendanceBean = attendanceBean;
    }

    @Override
    public String toString() {
        return "AttendanceType{" +
                "aNormal=" + aNormal +
                ", aLate=" + aLate +
                ", aLeaveEarly=" + aLeaveEarly +
                ", aLeave=" + aLeave +
                ", aAssentisson=" + aAssentisson +
                ", attendanceBean=" + attendanceBean +
                '}';
    }
}
