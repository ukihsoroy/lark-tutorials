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
public class UserCustomAttr {

    /**
     * 自定义属性类型
     */
    private String type;

    /**
     * 自定义属性ID
     */
    private String id;

    /**
     * 自定义属性取值
     */
    private UserCustomAttrValue value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserCustomAttrValue getValue() {
        return value;
    }

    public void setValue(UserCustomAttrValue value) {
        this.value = value;
    }
}
