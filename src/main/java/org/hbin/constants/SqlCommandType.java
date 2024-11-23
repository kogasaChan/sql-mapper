package org.hbin.constants;

public enum SqlCommandType {
    SELECT("select"),
    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete");

    private final String tagName;

    SqlCommandType(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public static SqlCommandType fromTagName(String tagName) {
        for (SqlCommandType type : values()) {
            if (type.getTagName().equalsIgnoreCase(tagName)) {
                return type;
            }
        }
        return null;
    }
}