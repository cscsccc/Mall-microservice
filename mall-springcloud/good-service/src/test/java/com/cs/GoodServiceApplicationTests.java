package com.cs;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.cs.config.ESConfig;
import com.cs.domain.po.MallGoods;
import com.cs.domain.po.MallGoodsDoc;
import com.cs.mapper.MallGoodsMapper;
import com.cs.util.BeanUtil;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GoodServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private MallGoodsMapper mallGoodsMapper;

    @Test
    void insertToIndex() throws IOException {

        // 获取商品信息
        List<MallGoods> mallGoods = mallGoodsMapper.getMallGoods();
//        List<MallGoodsDoc> mallGoodsDocs = new ArrayList<>();
//        for (MallGoods mallGood : mallGoods) {
//            MallGoodsDoc mallGoodsDoc = new MallGoodsDoc();
//            BeanUtil.copyProperties(mallGood, mallGoodsDoc);
//            mallGoodsDoc.setGoodsId(mallGood.getGoodsId().toString());
//            mallGoodsDoc.setGoodsCategoryId(mallGood.getGoodsCategoryId().toString());
//            mallGoodsDocs.add(mallGoodsDoc);
//        }

        BulkRequest bulkRequest = new BulkRequest();
        for (MallGoods mallGood : mallGoods) {
            bulkRequest.add(new IndexRequest("goods").id(JSON.toJSONString(mallGood.getGoodsId())).source(JSON.toJSONString(mallGood), XContentType.JSON));
        }
        client.bulk(bulkRequest, RequestOptions.DEFAULT);

    }

}
