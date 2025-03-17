package com.cs.mapper;

import com.cs.entity.po.MallOrder;
import com.cs.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallOrderMapper {

    List<MallOrder> selectMallOrderPageList(PageQueryUtil pageQueryUtil);

    int selectTotalMallOrder(PageQueryUtil pageQueryUtil);

    MallOrder selectMallOrderByOrderId(Long orderId);

    List<MallOrder> selectByPrimaryKeys(List<Long> list);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(List<Long> list);

    int checkOut(List<Long> list);

    int insertSelective(MallOrder order);

    MallOrder selectMallOrderByOrderNo(String orderNo);

    int updateBySelective(MallOrder mallOrder);

    List<MallOrder> getOrderListPage(PageQueryUtil pageQueryUtil);

    int getTotalList(PageQueryUtil pageQueryUtil);

    MallOrder selectMallOrderByOrderNoAnduserId(@Param("orderNo") String orderNo, @Param("userId") Long userId);
}
