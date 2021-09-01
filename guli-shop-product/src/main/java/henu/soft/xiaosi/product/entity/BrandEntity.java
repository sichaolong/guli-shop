package henu.soft.xiaosi.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;


import henu.soft.common.valid.SaveValidGroup;
import henu.soft.common.valid.UpdateValidGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 21:04:34
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@Null(message = "新增时不能指定id",groups = {SaveValidGroup.class})
	@NotNull(message = "更新时需要指定id",groups = {UpdateValidGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空！",groups = {SaveValidGroup.class,SaveValidGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "logo不能为空！",groups = {SaveValidGroup.class})
	@URL(message = "logo需要是一个合法的url地址",groups = {SaveValidGroup.class,UpdateValidGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(groups = {SaveValidGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母不能为空！",groups = {SaveValidGroup.class,UpdateValidGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空！",groups = {SaveValidGroup.class})
	@Min(value = 0,message = "排序必须大于0",groups = {SaveValidGroup.class,UpdateValidGroup.class})
	private Integer sort;

}
