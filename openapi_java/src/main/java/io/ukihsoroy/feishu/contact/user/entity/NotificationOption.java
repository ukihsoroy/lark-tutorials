package io.ukihsoroy.feishu.contact.user.entity;

import java.util.List;

/**
 * <p>创建用户的邀请方式</p>
 *
 * @author K.O
 * @email ko.shen@hotmail.com
 */
public class NotificationOption {

    /**
     * 通道列表，枚举值：sms（短信邀请），email（邮件邀请）
     */
    private List<String> channel;

    /**
     * 语言类型
     * 示例值："zh-CN"
     * 可选值有：
     * zh-CN：中文
     * en-US：英文
     * ja-JP：日文
     */
    private String language;

    public List<String> getChannel() {
        return channel;
    }

    public void setChannel(List<String> channel) {
        this.channel = channel;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
