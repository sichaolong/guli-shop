package henu.soft.xiaosi.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.ware.entity.WareInfoEntity;
import henu.soft.xiaosi.ware.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:36:29
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询运费
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);

}

