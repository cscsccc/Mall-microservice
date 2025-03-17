package com.cs.mapper;

import com.cs.entity.po.MallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallOrderItemMapper {

    List<MallOrderItem> selectItemsByOrderId(@Param("orderId") Long orderId);

    List<MallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    int insertBatch(@Param("mallOrderItems") List<MallOrderItem> mallOrderItems);

    int insertSelective(MallOrderItem mallOrderItem);
}
