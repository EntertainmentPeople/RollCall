package com.klj.rollcall.bean;

/**
 * 用户信息类
 */

public class UserBean extends BaseBean {
    private String userId;      //用户ID
    private String userPwd;     //用户密码
    private String userRole;    //用户角色

    /**
     * 无参构造
     */
    public UserBean() {
    }

    /**
     * 全参构造
     * @param userId
     * @param userPwd
     * @param userRole
     */
    public UserBean(String userId, String userPwd, String userRole) {
        this.userId = userId;
        this.userPwd = userPwd;
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "userId='" + userId + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}
