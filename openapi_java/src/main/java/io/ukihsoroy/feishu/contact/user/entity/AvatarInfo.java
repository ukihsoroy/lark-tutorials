package io.ukihsoroy.feishu.contact.user.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * <p>用户头像</p>
 *
 * @author K.O
 * @email ko.shen@hotmail.com
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) //开启驼峰转下划线
public class AvatarInfo {

    /**
     * 72*72像素头像链接
     */
    private String avatar_72;

    /**
     * 240*240像素头像链接
     */
    private String avatar_240;

    /**
     * 640*640像素头像链接
     */
    private String avatar_640;

    /**
     * 原始头像链接
     */
    private String avatarOrigin;

    public String getAvatar_72() {
        return avatar_72;
    }

    public void setAvatar_72(String avatar_72) {
        this.avatar_72 = avatar_72;
    }

    public String getAvatar_240() {
        return avatar_240;
    }

    public void setAvatar_240(String avatar_240) {
        this.avatar_240 = avatar_240;
    }

    public String getAvatar_640() {
        return avatar_640;
    }

    public void setAvatar_640(String avatar_640) {
        this.avatar_640 = avatar_640;
    }

    public void setAvatarOrigin(String avatarOrigin) {
        this.avatarOrigin = avatarOrigin;
    }

    public String getAvatarOrigin() {
        return avatarOrigin;
    }
}
