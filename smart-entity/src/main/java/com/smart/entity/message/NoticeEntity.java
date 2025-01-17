package com.smart.entity.message;


import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.model.excel.annotation.ExcelField;
import com.smart.model.excel.annotation.ExcelFields;
import com.smart.mybatis.entity.BaseEntity;
import com.smart.mybatis.annotation.Column;
import com.smart.mybatis.enums.QueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.NoArgsConstructor;

/**
 * 通知公告
 *
 * @author wf
 * @since 2024-12-13 17:10:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@TableName("sys_notice")
@ExcelFields({
        @ExcelField(title = "标题", attrName = "noticeTitle",  align = ExcelField.Align.CENTER, sort = 1),
		@ExcelField(title = "类别", attrName = "noticeCategory",  dictCode = "sys_notice_category", align = ExcelField.Align.CENTER, sort = 2),
		@ExcelField(title = "类型", attrName = "noticeType",  dictCode = "sys_notice_type", align = ExcelField.Align.CENTER, sort = 3),
		@ExcelField(title = "内容", attrName = "noticeContent",  align = ExcelField.Align.CENTER, sort = 4, words = 80)
})
public class NoticeEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * 标题
	 */
    @Column(name = "notice_title", queryType = QueryType.LIKE)
    private String noticeTitle;
	/**
	 * 内容
	 */
    private String noticeContent;
	/**
	 * 类型（1普通 2紧急）
	 */
    @Column(name = "notice_type", queryType = QueryType.EQ)
    private String noticeType;
	/**
	 * 类别（1公告 2通知）
	 */
    @Column(name = "notice_category", queryType = QueryType.EQ)
    private String noticeCategory;

}
