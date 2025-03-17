package com.cs.service;

import com.cs.entity.po.MallSeckill;
import com.cs.entity.vo.SeckillSuccessVO;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;

public interface MallSecKillService {
    PageResult findSecKillList(PageQueryUtil pageUtil);

    PageResult findSecKillListForAdminPage(PageQueryUtil pageUtil);

    MallSeckill findSecKillByPrimaryKey(Long seckillId);

    boolean updateSeckill(MallSeckill mallSeckill);

    boolean deleteSeckillByPrimaryKeys(Long[] ids);

    boolean saveSeckill(MallSeckill mallSeckill);

    SeckillSuccessVO executeSeckill(Long seckillId, Long userId);
}
