package com.cloudslip.usermanagement.model;

import com.cloudslip.usermanagement.enums.ServerSettingGroup;
import com.cloudslip.usermanagement.enums.ServerSettingKey;
import com.cloudslip.usermanagement.enums.ServerSettingType;

public class ServerSetting extends BaseEntity {

    private ServerSettingGroup group;
    private ServerSettingKey key;
    private String defaultValue;
    private String currentValue;
    private ServerSettingType type = ServerSettingType.VISIBLE;

    public ServerSetting() {
    }

    public ServerSetting(ServerSettingGroup group, ServerSettingKey key, String defaultValue, String currentValue, ServerSettingType type) {
        this.group = group;
        this.key = key;
        this.defaultValue = defaultValue;
        this.currentValue = currentValue;
        this.type = type;
    }

    public ServerSettingGroup getGroup() {
        return group;
    }

    public void setGroup(ServerSettingGroup group) {
        this.group = group;
    }

    public ServerSettingKey getKey() {
        return key;
    }

    public void setKey(ServerSettingKey key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public ServerSettingType getType() {
        return type;
    }

    public void setType(ServerSettingType type) {
        this.type = type;
    }
}
