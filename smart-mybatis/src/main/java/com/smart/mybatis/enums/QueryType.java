package com.smart.mybatis.enums;

/**
 * SQL查询条件
 *
 * @author wf
 * @since 2022-07-26 00:00:00
 */
public enum QueryType {
    /**
     * 查询条件 - IN,NOT_IN,BETWEEN,LIKE,EQ,NE,GT,GE,LT,LE
     */
    NONE(),
    IN(),
    NOT_IN(),
    BETWEEN(),
    LIKE(),
    EQ(),
    NE(),
    GT(),
    GE(),
    LT(),
    LE(),
    LIKE_IN_AND(),
    LIKE_IN_OR();

    QueryType() {
    }
}