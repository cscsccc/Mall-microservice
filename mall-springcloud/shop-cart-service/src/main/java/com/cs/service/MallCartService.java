package com.cs.service;


import com.cs.dto.MallCartItemVO;
import com.cs.param.MallCartEditCountParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallCartService {
    List<MallCartItemVO> getCartItemList(Long userId);

    String addCart(Integer goodsCount, Long goodsId, Long userId);

    String updatecartItemCount(MallCartEditCountParam mallCartEditCountParam, Long userId);

    boolean deleteCartItem(Long id, Long userId);

    List<MallCartItemVO> getCartItemListForSettle(Long[] cartItemIds);

    List<MallCartItemVO> findCartItemListForSettle(@Param("cartItemIds") List<Long> cartItemIds, @Param("userId") Long userId);

    int deleteBatchByPrimary(List<Long> itemIdList);
}
