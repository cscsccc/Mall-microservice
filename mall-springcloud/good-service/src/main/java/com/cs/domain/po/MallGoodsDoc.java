package com.cs.domain.po;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@Document(indexName = "goods")  // 指定与 Elasticsearch 索引对应
public class MallGoodsDoc { // 用于指定文档的唯一 ID
    private String goodsId;

    private String goodsName;

    private String goodsIntro;

    private String goodsCategoryId;

    private String goodsCoverImg;

    private String goodsCarousel;

    private Integer originalPrice;

    private Integer sellingPrice;

    private Integer stockNum;

    private String tag;

    private Byte goodsSellStatus;

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String goodsDetailContent;
}

