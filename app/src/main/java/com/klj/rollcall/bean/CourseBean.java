package com.klj.rollcall.bean;

/**
 * 课程信息类
 */

public class CourseBean extends BaseBean {
    private String cNo;//课程编号
    private String cName;   //课程名称
    private String tcPlace;   //上课教室
    private String tName; //教师
    private String tcYear; //开始学年
    private String tcTerm; //学期
    private String tcWeek; //上课周次
    private int tcWeekday; //上课星期
    private int tcStart;  //开始上课节次
    private int tcStep; //一共几节课
    private float cScore;//学分
    private int cPeriod; //课时
    private String cDname;//开课院系
    private String tcClass;//临时班级
    private int tcStudentNum;//人数

    public CourseBean() {
    }

    public CourseBean(String cNo, String cName, String tcPlace, String tName, String tcWeek, int tcWeekday, int tcStart, int tcStep, float cScore, int cPeriod) {
        this.cNo = cNo;
        this.cName = cName;
        this.tcPlace = tcPlace;
        this.tName = tName;
        this.tcWeek = tcWeek;
        this.tcWeekday = tcWeekday;
        this.tcStart = tcStart;
        this.tcStep = tcStep;
        this.cScore = cScore;
        this.cPeriod = cPeriod;
    }

    public CourseBean(String cNo, String cName, String tcPlace, String tName, String tcYear,
                      String tcTerm, String tcWeek, int tcWeekday, int tcStart,
                      int tcStep, float cScore, int cPeriod, String cDname, String tcClass,
                      int tcStudentNum) {
        this.cNo = cNo;
        this.cName = cName;
        this.tcPlace = tcPlace;
        this.tName = tName;
        this.tcYear = tcYear;
        this.tcTerm = tcTerm;
        this.tcWeek = tcWeek;
        this.tcWeekday = tcWeekday;
        this.tcStart = tcStart;
        this.tcStep = tcStep;
        this.cScore = cScore;
        this.cPeriod = cPeriod;
        this.cDname = cDname;
        this.tcClass = tcClass;
        this.tcStudentNum = tcStudentNum;
    }

    public String getcNo() {
        return cNo;
    }

    public void setcNo(String cNo) {
        this.cNo = cNo;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getTcPlace() {
        return tcPlace;
    }

    public void setTcPlace(String tcPlace) {
        this.tcPlace = tcPlace;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public String getTcYear() {
        return tcYear;
    }

    public void setTcYear(String tcStartYear) {
        this.tcYear = tcYear;
    }

    public String getTcTerm() {
        return tcTerm;
    }

    public void setTcTerm(String tcTerm) {
        this.tcTerm = tcTerm;
    }

    public String getTcWeek() {
        return tcWeek;
    }

    public void setTcWeek(String tcWeek) {
        this.tcWeek = tcWeek;
    }

    public int getTcWeekday() {
        return tcWeekday;
    }

    public void setTcWeekday(int tcWeekday) {
        this.tcWeekday = tcWeekday;
    }

    public int getTcStart() {
        return tcStart;
    }

    public void setTcStart(int tcStart) {
        this.tcStart = tcStart;
    }

    public int getTcStep() {
        return tcStep;
    }

    public void setTcStep(int tcStep) {
        this.tcStep = tcStep;
    }

    public float getcScore() {
        return cScore;
    }

    public void setcScore(float cScore) {
        this.cScore = cScore;
    }

    public int getcPeriod() {
        return cPeriod;
    }

    public void setcPeriod(int cPeriod) {
        this.cPeriod = cPeriod;
    }

    public String getTcClass() {
        return tcClass;
    }

    public void setTcClass(String tcClass) {
        this.tcClass = tcClass;
    }

    public int getTcStudentNum() {
        return tcStudentNum;
    }

    public void setTcStudentNum(int tcStudentNum) {
        this.tcStudentNum = tcStudentNum;
    }

    public String getcDname() {
        return cDname;
    }

    public void setcDname(String cDname) {
        this.cDname = cDname;
    }

    @Override
    public String toString() {
        return "CourseBean{" +
                "cNo='" + cNo + '\'' +
                ", cName='" + cName + '\'' +
                ", tcPlace='" + tcPlace + '\'' +
                ", tName='" + tName + '\'' +
                ", tcYear=" + tcYear +
                ", tcTerm=" + tcTerm +
                ", tcWeek='" + tcWeek + '\'' +
                ", tcWeekday=" + tcWeekday +
                ", tcStart=" + tcStart +
                ", tcStep=" + tcStep +
                ", cScore=" + cScore +
                ", cPeriod=" + cPeriod +
                ", cDname='" + cDname + '\'' +
                ", tcClass='" + tcClass + '\'' +
                ", tcStudentNum=" + tcStudentNum +
                '}';
    }
}
