package io.ukihsoroy.feishu.contact.user.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * <p></p>
 *
 * @author K.O
 * @email ko.shen@hotmail.com
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) //开启驼峰转下划线
public class UserResponse extends UserRequest {

    /**
     * 用户的union_id
     */
    private String unionId;

    /**
     * 用户的open_id
     */
    private String openId;

    /**
     * 用户头像信息
     */
    private AvatarInfo avatar;

    /**
     * 用户状态
     */
    private UserStatus status;

    private String description;

    /**
     * 是否是租户管理员
     */
    private Boolean isTenantManager;

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public AvatarInfo getAvatar() {
        return avatar;
    }

    public void setAvatar(AvatarInfo avatar) {
        this.avatar = avatar;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsTenantManager() {
        return isTenantManager;
    }

    public void setIsTenantManager(Boolean tenantManager) {
        isTenantManager = tenantManager;
    }
}
