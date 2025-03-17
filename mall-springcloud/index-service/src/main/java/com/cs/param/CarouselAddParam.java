package com.cs.param;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CarouselAddParam {

    @NotEmpty(message = "轮播图URL不能为空")
    private String carouselUrl;

    @NotEmpty(message = "轮播图跳转地址不能为空")
    private String redirectUrl;

    @Min(value = 1, message = "carouselRank最低为1")
    @Max(value = 200, message = "carouselRank最高为200")
    @NotNull(message = "carouselRank不能为空")
    private Integer carouselRank;
}
