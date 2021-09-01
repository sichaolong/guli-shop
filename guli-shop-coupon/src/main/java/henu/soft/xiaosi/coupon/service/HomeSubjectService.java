package henu.soft.xiaosi.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.coupon.entity.HomeSubjectEntity;

import java.util.Map;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:30:21
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

