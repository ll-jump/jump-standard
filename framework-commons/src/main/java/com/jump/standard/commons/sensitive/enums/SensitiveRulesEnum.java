package com.jump.standard.commons.sensitive.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 脱敏类型枚举
 */
public enum SensitiveRulesEnum {
    DESENSITIZED_FULLY("DESENSITIZED_FULLY", "任一字段全脱敏", "任意类型", SensitiveRulesEnum.RuleEnum.RULE_DESENSITIZED_FULLY),
    DISSHOW_FIELD("DISSHOW_FIELD", "企业密钥|个人密钥|口令数据等", "不显示", SensitiveRulesEnum.RuleEnum.RULE_DISSHOW),
    NAME("NAME", "姓名", "个人信息", SensitiveRulesEnum.RuleEnum.RULE_NAME),
    PASSWORD("PASSWORD", "密码", "个人信息", SensitiveRulesEnum.RuleEnum.RULE_PASSWORD),
    CARD_NO("CARD_NO", "银行卡号", "个人信息", SensitiveRulesEnum.RuleEnum.RULE_CARD_NO),
    CERTI_NO("CERTI_NO", "证件号", "个人信息", SensitiveRulesEnum.RuleEnum.RULE_CERTI_NO),
    PHONE_NO("PHONE_NO", "手机号", "个人信息", SensitiveRulesEnum.RuleEnum.RULE_PHONE_NO),
    EMAIL("EMAIL", "邮箱", "个人信息", SensitiveRulesEnum.RuleEnum.RULE_EMAIL),
    HASH("HASH", "hash值", "hash值", SensitiveRulesEnum.RuleEnum.RULE_HASH);

    private String name;
    private String dataField;
    private String dataType;
    private SensitiveRulesEnum.RuleEnum rule;

    public static SensitiveRulesEnum getSensitiveRule(String name) {
        SensitiveRulesEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            SensitiveRulesEnum rule = var1[var3];
            if (rule.getName().equalsIgnoreCase(StringUtils.trim(name))) {
                return rule;
            }
        }

        return null;
    }

    public String getName() {
        return this.name;
    }

    public String getDataField() {
        return this.dataField;
    }

    public String getDataType() {
        return this.dataType;
    }

    public SensitiveRulesEnum.RuleEnum getRule() {
        return this.rule;
    }

    private SensitiveRulesEnum(String name, String dataField, String dataType, SensitiveRulesEnum.RuleEnum rule) {
        this.name = name;
        this.dataField = dataField;
        this.dataType = dataType;
        this.rule = rule;
    }

    public static enum RuleEnum {
        RULE_DESENSITIZED_FULLY(true, 6, 0, 0),
        RULE_DESENSITIZED_1_(true, 1, 1, 0),
        RULE_NAME(true, 2, 1, 0),
        RULE_PASSWORD(true, 6, 0, 0),
        RULE_CARD_NO(true, 4, 4, 4),
        RULE_CERTI_NO(true, 16, 1, 1),
        RULE_PHONE_NO(true, 6, 3, 2),
        RULE_EMAIL(true, 3, 2, 1),
        RULE_DISSHOW(false, 0, 0, 0),
        RULE_HASH(true, 0, 0, 0);

        private boolean isShow;
        private int paddingStar;
        private int beforeIndex;
        private int afterIndex;

        public boolean isShow() {
            return this.isShow;
        }

        public int getPaddingStar() {
            return this.paddingStar;
        }

        public int getBeforeIndex() {
            return this.beforeIndex;
        }

        public int getAfterIndex() {
            return this.afterIndex;
        }

        private RuleEnum(boolean isShow, int paddingStar, int beforeIndex, int afterIndex) {
            this.isShow = isShow;
            this.paddingStar = paddingStar;
            this.beforeIndex = beforeIndex;
            this.afterIndex = afterIndex;
        }
    }
}
