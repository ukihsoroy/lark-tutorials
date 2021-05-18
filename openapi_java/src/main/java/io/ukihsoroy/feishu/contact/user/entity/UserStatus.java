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
public class UserStatus {

    /**
     * 是否冻结
     */
    private boolean isFrozen;

    /**
     * 是否离职
     */
    private boolean isResigned;

    /**
     * 是否激活
     */
    private boolean isActivated;


    public void setIsFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public boolean getIsFrozen() {
        return isFrozen;
    }

    public void setIsResigned(boolean isResigned) {
        this.isResigned = isResigned;
    }

    public boolean getIsResigned() {
        return isResigned;
    }

    public void setIsActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public boolean getIsActivated() {
        return isActivated;
    }

}
