package com.klj.rollcall.bean;

/**
 * Created by 娱乐人物 on 2017/4/18.
 */

public class StudentBean extends BaseBean {
    private String sNo;             //学号
    private String sName;           //姓名
    private String sSex;            //性别
    private int sAge;               //年龄
    private String mName;           //专业
    private int sGrade;             //年级
    private String dName;           //院系
    private String sPhone;          //联系电话
    private String sPortrait;       //头像
    private int scId;

    public StudentBean() {
    }

    public StudentBean(String sNo, String sName, String sSex, int sAge, String mName, int sGrade, String dName, String sPhone, String sPortrait) {
        this.sNo = sNo;
        this.sName = sName;
        this.sSex = sSex;
        this.sAge = sAge;
        this.mName = mName;
        this.sGrade = sGrade;
        this.dName = dName;
        this.sPhone = sPhone;
        this.sPortrait = sPortrait;
    }

    public StudentBean(String sNo, String sName, String sSex, int sAge, String mName, int sGrade, String dName, String sPhone, String sPortrait, int scId) {
        this.sNo = sNo;
        this.sName = sName;
        this.sSex = sSex;
        this.sAge = sAge;
        this.mName = mName;
        this.sGrade = sGrade;
        this.dName = dName;
        this.sPhone = sPhone;
        this.sPortrait = sPortrait;
        this.scId = scId;
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

    public int getsAge() {
        return sAge;
    }

    public void setsAge(int sAge) {
        this.sAge = sAge;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getsGrade() {
        return sGrade;
    }

    public void setsGrade(int sGrade) {
        this.sGrade = sGrade;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String getsPhone() {
        return sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    public String getsPortrait() {
        return sPortrait;
    }

    public void setsPortrait(String sPortrait) {
        this.sPortrait = sPortrait;
    }

    public int getScId() {
        return scId;
    }

    public void setScId(int scId) {
        this.scId = scId;
    }

    @Override
    public String toString() {
        return "StudentBean{" +
                "sNo='" + sNo + '\'' +
                ", sName='" + sName + '\'' +
                ", sSex='" + sSex + '\'' +
                ", sAge=" + sAge +
                ", mName='" + mName + '\'' +
                ", sGrade=" + sGrade +
                ", dName='" + dName + '\'' +
                ", sPhone=" + sPhone +
                ", sPortrait=" + sPortrait +
                '}';
    }
}
