package com.smart.entity.message;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelFields;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.entity.BaseIdEntity;
import java.util.Date;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.enums.QueryType;
import com.smart.entity.file.FileEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.NoArgsConstructor;

/**
 * 通知公告发布记录
 *
 * @author wf
 * @since 2024-12-13 17:46:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_notice_record")
@ExcelFields({
        @ExcelField(title = "通知公告ID", attrName = "noticeId",  align = ExcelField.Align.CENTER, sort = 1),
        @ExcelField(title = "发布时间", attrName = "releaseTime",  align = ExcelField.Align.CENTER, sort = 2),
        @ExcelField(title = "发布类型", attrName = "releaseType",  dictCode = "sys_release_type", align = ExcelField.Align.CENTER, sort = 3),
        @ExcelField(title = "发布数据IDS", attrName = "releaseValue",  align = ExcelField.Align.CENTER, sort = 4)
})
public class NoticeRecordEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * 通知公告ID
	 */
    @Column(name = "notice_id", queryType = QueryType.EQ)
    private String noticeId;
	/**
	 * 发布时间
	 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone="GMT+8")
    private Date releaseTime;
	/**
	 * 发布类型
	 */
    @Column(name = "release_type", queryType = QueryType.EQ)
    private String releaseType;
	/**
	 * 发布数据IDS
	 */
    private String releaseValue;

    /**
     * 发布时间Between查询集合
     */
    @Column(name = "release_time", queryType = QueryType.BETWEEN)
    @TableField(exist = false)
    private List<String> releaseTimeBetweenList;

}
