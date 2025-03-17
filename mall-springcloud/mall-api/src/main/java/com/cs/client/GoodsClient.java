package com.cs.client;

import com.cs.dto.MallGoods;
import com.cs.dto.StockNumDTO;
import com.cs.fallback.GoodClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "good-service")
public interface GoodsClient {

    @GetMapping("/api/getListById")
    List<MallGoods> getListByIds(@RequestParam("goodsIds") List<Long> goodsIds);

    @GetMapping("/api/getMallGoodById")
    MallGoods getOneById(@RequestParam("goodsId") Long goodsId);

    @PostMapping("/api/deductStockNum")
    int deductStockNum(@RequestBody StockNumDTO stockNumDTO);

    @PostMapping("/api/recoverStockNum")
    int recoverStockNum(@RequestBody StockNumDTO stockNumDTO);
}
