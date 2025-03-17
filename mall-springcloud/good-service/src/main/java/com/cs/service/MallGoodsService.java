package com.cs.service;

import com.cs.domain.po.GoodsCategory;
import com.cs.domain.po.MallGoods;
import com.cs.domain.vo.MallGoodsDetailVO;
import com.cs.dto.StockNumDTO;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;

import java.util.List;

public interface MallGoodsService {
    String saveGood(MallGoods mallGoods);

    PageResult getMallGoodsPage(PageQueryUtil pageUtil);

    int updateBatchSellStatus(Long[] ids, int sellStatus);

    MallGoods getOneById(Long id);

    GoodsCategory getCategoryById(Long id);

    String updateMallGoods(MallGoods goods);

    MallGoodsDetailVO getDetail(Long goodsId);

    PageResult searchMallGoods(PageQueryUtil pageQueryUtil);

    List<MallGoods> getListByIds(List<Long> goodsIds);

    int deductStockNum(StockNumDTO stockNumDTO);

    int recoverStockNum(StockNumDTO stockNumDTO);
}
