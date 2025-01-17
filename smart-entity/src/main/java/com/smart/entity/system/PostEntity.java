package com.smart.entity.system;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * 岗位
 *
 * @author wf
 * @since 2022-03-12 08:42:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_post")
public class PostEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 岗位类型
     */
    private Integer category;

    /**
     * 岗位编号
     */
    @Column(name = "post_code", queryType = QueryType.LIKE)
    private String postCode;

    /**
     * 岗位名称
     */
    @Column(name = "post_name", queryType = QueryType.LIKE)
    private String postName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 启用状态
     */
    @Column(queryType = QueryType.EQ)
    private String status;

}
