package com.gome.iuv.dao.enums;

/**
 * 公共的枚举类
 */
public enum CommonEnum {
    YES("是", 1), NO("否", 0);

    // 成员变量
    private String name;

    private int index;

    // 构造方法
    private CommonEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (CommonEnum c : CommonEnum.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}