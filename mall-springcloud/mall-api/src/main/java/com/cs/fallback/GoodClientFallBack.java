package com.cs.fallback;

import com.cs.client.GoodsClient;
import com.cs.dto.MallGoods;
import com.cs.dto.StockNumDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

@Slf4j
public class GoodClientFallBack implements FallbackFactory<GoodsClient> {

    @Override
    public GoodsClient create(Throwable cause) {
        return new GoodsClient() {

            @Override
            public List<MallGoods> getListByIds(List<Long> goodsIds) {
                log.error("未根据批量id找到商品数据", cause);
                return List.of();
            }

            @Override
            public MallGoods getOneById(Long goodsId) {
                log.error("未根据单个id找到商品", cause);
                return null;
            }

            @Override
            public int deductStockNum(StockNumDTO stockNumDTO) {
                log.error("未扣减成功", cause);
                return 0;
            }

            @Override
            public int recoverStockNum(StockNumDTO stockNumDTO) {
                log.error("恢复库存失败", cause);
                return 0;
            }
        };
    }
}
