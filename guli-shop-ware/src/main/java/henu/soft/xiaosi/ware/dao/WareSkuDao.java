package henu.soft.xiaosi.ware.dao;

import henu.soft.xiaosi.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:36:29
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {


    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Integer getTotalStock(Long id);

    /**
     * //找出所有库存大于商品数的仓库，风机装起来
     * @param skuId
     * @param count
     * @return
     */

    List<Long> listWareIdsHasStock(@Param("skuId") Long skuId, @Param("count") Integer count);

    /**
     * 一个个锁定仓库
     * @param skuId
     * @param num
     * @param wareId
     * @return
     */
    Long lockWareSku(@Param("skuId") Long skuId, @Param("num") Integer num, @Param("wareId") Long wareId);


    /**
     * 库存回滚
     * @param skuId
     * @param skuNum
     * @param wareId
     */
    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum, @Param("wareId") Long wareId);

}
