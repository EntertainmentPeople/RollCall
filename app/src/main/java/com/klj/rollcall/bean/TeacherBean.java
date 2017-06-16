package com.klj.rollcall.bean;

/**
 * Created by 娱乐人物 on 2017/4/18.
 */

public class TeacherBean extends BaseBean {
    private String tNo;         //工号
    private String tName;       //姓名
    private String tSex;        //性别
    private int tAge;        //年龄
    private String dName;       //院系/部门
    private String tPhone;      //联系电话
    private String tPortrait;   //头像

    public TeacherBean() {
    }

    public TeacherBean(String tNo, String tName, String tSex, int tAge, String dName, String tPhone, String tPortrait) {
        this.tNo = tNo;
        this.dName = dName;
        this.tSex = tSex;
        this.tAge = tAge;
        this.tName = tName;
        this.tPhone = tPhone;
        this.tPortrait = tPortrait;
    }

    public String gettNo() {
        return tNo;
    }

    public void settNo(String tNo) {
        this.tNo = tNo;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public String gettSex() {
        return tSex;
    }

    public void settSex(String tSex) {
        this.tSex = tSex;
    }

    public int gettAge() {
        return tAge;
    }

    public void settAge(int tAge) {
        this.tAge = tAge;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String gettPhone() {
        return tPhone;
    }

    public void settPhone(String tPhone) {
        this.tPhone = tPhone;
    }

    public String gettPortrait() {
        return tPortrait;
    }

    public void settPortrait(String tPortrait) {
        this.tPortrait = tPortrait;
    }

    @Override
    public String toString() {
        return "TeacherBean{" +
                "tNo='" + tNo + '\'' +
                ", tName='" + tName + '\'' +
                ", tSex=" + tSex +
                ", tAge=" + tAge +
                ", dName='" + dName + '\'' +
                ", tPhone=" + tPhone +
                ", tPortrait=" + tPortrait +
                '}';
    }
}
