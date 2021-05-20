package io.ukihsoroy.feishu.contact.user.entity;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * <p></p>
 *
 * @author K.O
 * @email ko.shen@hotmail.com
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) //开启驼峰转下划线
public class UserView {

    private UserResponse user;

    private List<UserResponse> items;

    private Boolean hasMore;

    private String pageToken;

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public List<UserResponse> getItems() {
        return items;
    }

    public void setItems(List<UserResponse> items) {
        this.items = items;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getPageToken() {
        return pageToken;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }
}
