package io.ukihsoroy.feishu.contact.user.entity;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * <p>创建用户请求参数</p>
 *
 * @author K.O
 * @email ko.shen@hotmail.com
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) //开启驼峰转下划线
public class UserRequest {

    /**
     * 租户内用户的唯一标识
     */
    private String userId;

    /**
     * 用户名
     */
    private String name;

    /**
     * 英文名
     */
    private String enName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 手机号码可见性，true 为可见，false 为不可见，目前默认为 true。不可见时，组织员工将无法查看该员工的手机号码
     */
    private Boolean mobileVisible;

    /**
     * 性别
     * 示例值：1
     * 可选值有：
     * 0：保密
     * 1：男
     * 2：女
     */
    private Integer gender;

    /**
     * 头像的文件Key
     */
    private String avatarKey;

    /**
     * 用户所属部门的ID列表
     */
    private String[] departmentIds;

    /**
     * 用户的直接主管的用户ID
     */
    private String leaderUserId;

    /**
     * 城市
     */
    private String city;

    /**
     * 国家
     */
    private String country;

    /**
     * 工位
     */
    private String workStation;

    /**
     * 入职时间
     */
    private Integer joinTime;

    /**
     * 工号
     */
    private String employeeNo;

    /**
     * 员工类型
     */
    private Integer employeeType;

    /**
     * 用户排序信息
     */
    private List<UserOrder> orders;

    /**
     * 自定义属性
     */
    private List<UserCustomAttr> customAttrs;

    /**
     * 企业邮箱，请先确保已在管理后台启用飞书邮箱服务
     */
    private String enterpriseEmail;

    /**
     * 是否发送提示消息
     */
    private Boolean needSendNotification;

    /**
     * 创建用户的邀请方式
     */
    private NotificationOption notificationOption;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getMobileVisible() {
        return mobileVisible;
    }

    public void setMobileVisible(Boolean mobileVisible) {
        this.mobileVisible = mobileVisible;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAvatarKey() {
        return avatarKey;
    }

    public void setAvatarKey(String avatarKey) {
        this.avatarKey = avatarKey;
    }

    public String[] getDepartmentIds() {
        return departmentIds;
    }

    public void setDepartmentIds(String[] departmentIds) {
        this.departmentIds = departmentIds;
    }

    public String getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(String leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWorkStation() {
        return workStation;
    }

    public void setWorkStation(String workStation) {
        this.workStation = workStation;
    }

    public Integer getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Integer joinTime) {
        this.joinTime = joinTime;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public Integer getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(Integer employeeType) {
        this.employeeType = employeeType;
    }

    public List<UserOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<UserOrder> orders) {
        this.orders = orders;
    }

    public List<UserCustomAttr> getCustomAttrs() {
        return customAttrs;
    }

    public void setCustomAttrs(List<UserCustomAttr> customAttrs) {
        this.customAttrs = customAttrs;
    }

    public String getEnterpriseEmail() {
        return enterpriseEmail;
    }

    public void setEnterpriseEmail(String enterpriseEmail) {
        this.enterpriseEmail = enterpriseEmail;
    }

    public Boolean getNeedSendNotification() {
        return needSendNotification;
    }

    public void setNeedSendNotification(Boolean needSendNotification) {
        this.needSendNotification = needSendNotification;
    }

    public NotificationOption getNotificationOption() {
        return notificationOption;
    }

    public void setNotificationOption(NotificationOption notificationOption) {
        this.notificationOption = notificationOption;
    }
}
