package com.cs.client;

import com.cs.dto.GetListParam;
import com.cs.dto.MallCartItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "shop-cart-service")
public interface CartClient {

    @PostMapping("/api/deleteBatch")
    int deleteBatchByPrimaryKey(@RequestParam("itemIdList") List<Long> itemIdList);

    @PostMapping("/api/getListCartItems")
    List<MallCartItemVO> getListCartItems(@RequestBody GetListParam getListParam);
}