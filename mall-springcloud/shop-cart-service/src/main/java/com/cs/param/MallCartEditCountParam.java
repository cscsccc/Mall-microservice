package com.cs.param;
import lombok.Data;

@Data
public class MallCartEditCountParam {
    private Long cartItemId;
    private Integer goodsCount;
}
