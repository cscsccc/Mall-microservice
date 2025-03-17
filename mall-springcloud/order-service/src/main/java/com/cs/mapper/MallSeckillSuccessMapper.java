package com.cs.mapper;

import com.cs.entity.po.MallSeckillSuccess;
import org.apache.ibatis.annotations.Param;

public interface MallSeckillSuccessMapper {
    MallSeckillSuccess findByUserIdAndSeckillId(@Param("userId") Long userId, @Param("seckillId") Long seckillId);

    MallSeckillSuccess findByUserIdAndSeckillSuccessId(@Param("userId") Long userId, @Param("seckillSuccessId") Long seckillSuccessId);
}
