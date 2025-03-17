package com.cs.mapper;

import com.cs.entity.po.MallSeckill;
import com.cs.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MallSecKillMapper {
    List<MallSeckill> findSecKillList();

    int findSecKillListTotal(PageQueryUtil pageUtil);

    MallSeckill findSeckillByPrimaryKey(Long seckillId);

    int updateSecKillBySelective(MallSeckill mallSeckill);

    int deleteSeckillByPrimaryKeys(@Param("seckillIds") Long[] seckillIds);

    int saveSeckill(MallSeckill mallSeckill);

    List<MallSeckill> findSecKillListForAdmin(PageQueryUtil pageUtil);

    void killByProcedure(Map params);

    Long getPrimaryKryByGoodsId(Long goodsId);

    MallSeckill findSecKillByGoodsId(Long goodsId);
}
