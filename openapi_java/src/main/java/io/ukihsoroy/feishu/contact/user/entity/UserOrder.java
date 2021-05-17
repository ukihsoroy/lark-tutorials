package io.ukihsoroy.feishu.contact.user.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * <p>用户排序</p>
 *
 * @author K.O
 * @email ko.shen@hotmail.com
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) //开启驼峰转下划线
public class UserOrder {

    private String departmentId;

    private Integer userOrder;

    private Integer departmentOrder;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getUserOrder() {
        return userOrder;
    }

    public void setUserOrder(Integer userOrder) {
        this.userOrder = userOrder;
    }

    public Integer getDepartmentOrder() {
        return departmentOrder;
    }

    public void setDepartmentOrder(Integer departmentOrder) {
        this.departmentOrder = departmentOrder;
    }
}
