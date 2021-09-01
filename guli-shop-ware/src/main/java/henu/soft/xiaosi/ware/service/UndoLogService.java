package henu.soft.xiaosi.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:36:29
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

