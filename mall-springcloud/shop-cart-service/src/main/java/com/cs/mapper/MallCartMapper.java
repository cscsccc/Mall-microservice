package com.cs.mapper;

import com.cs.domain.po.MallCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallCartMapper {
    List<MallCartItem> selectByUserId(@Param("userId") Long userId, @Param("number") int number);

    MallCartItem selectByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    int saveCart(MallCartItem temp);

    int selectCountByUserId(Long userId);

    MallCartItem selectByPrimaryKey(Long cartItemId);

    int updateByPrimaryKeySelective(MallCartItem mallCartItem);

    int deleteByPrimaryKey(Long cartItemId);

    List<MallCartItem> selectByPrimaryKeyForSettle(@Param("cartItemIds") Long[] cartItemIds);

    List<MallCartItem> selectByUserIDAndCartItems(@Param("cartItemIds") List<Long> cartItemIds, @Param("userId") Long userId);

    int deleteBatchByPrimaryKey(@Param("itemIdList") List<Long> itemIdList);

    List<MallCartItem> selectByOrderId(Long orderId);
}
