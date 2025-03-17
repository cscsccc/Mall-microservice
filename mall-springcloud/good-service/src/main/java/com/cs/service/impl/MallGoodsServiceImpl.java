package com.cs.service.impl;

import com.alibaba.fastjson.JSON;
import com.cs.common.Constants;
import com.cs.common.MallCategoryLevelEnum;
import com.cs.common.MallException;
import com.cs.common.ServiceResultEnum;
import com.cs.domain.po.GoodsCategory;
import com.cs.domain.po.MallGoods;
import com.cs.domain.po.MallGoodsDoc;
import com.cs.domain.vo.MallGoodsDetailVO;
import com.cs.domain.vo.MallSearchGoodsVO;
import com.cs.dto.StockNumDTO;
import com.cs.mapper.GoodsCategoryMapper;
import com.cs.mapper.MallGoodsMapper;
import com.cs.service.MallGoodsService;
import com.cs.util.BeanUtil;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallGoodsServiceImpl implements MallGoodsService {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;
    @Autowired
    private MallGoodsMapper goodsMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public String saveGood(MallGoods mallGoods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(mallGoods.getGoodsCategoryId());

        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }

        // 查找有无重名商品 分类id name
        if (goodsMapper.selectByIdAndName(goodsCategory.getCategoryId(), mallGoods.getGoodsName()) != null){
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }

        if (goodsMapper.saveGood(mallGoods) > 0){
            try {
                MallGoodsDoc mallGoodsDoc = new MallGoodsDoc();
                BeanUtil.copyProperties(mallGoods, mallGoodsDoc);
                mallGoodsDoc.setGoodsId(mallGoods.getGoodsId().toString());
                mallGoodsDoc.setGoodsCategoryId(mallGoods.getGoodsCategoryId().toString());

                System.out.println(mallGoodsDoc);
                // 创建索引请求
                IndexRequest indexRequest = new IndexRequest("goods")
                        .id(mallGoods.getGoodsId().toString())  // 设置商品 ID
                        .source(JSON.toJSONString(mallGoodsDoc), XContentType.JSON);  // 设置商品文档内容

                // 执行请求
                IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
                if (indexResponse.status() == RestStatus.CREATED){
                    return ServiceResultEnum.SUCCESS.getResult();
                }else {
                    log.error("保存商品到索引库失败:{}", indexResponse);
                    return "保存到索引库失败，请介入";
                }
            } catch (IOException e) {
                if (!e.getMessage().contains("Created")){
                    e.printStackTrace();
                    log.error("创建到索引库失败,{}", e.getMessage());
                    throw new MallException("创建到索引库失败，请检查后台信息");
                }
                // 如果同步到 Elasticsearch 失败，可以返回错误或忽略
                log.info("虽然报错 但保存{}到索引库成功（版本问题）", mallGoods);
                return ServiceResultEnum.SUCCESS.getResult();
            }
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public PageResult getMallGoodsPage(PageQueryUtil pageUtil) {
        List<MallGoods> mallGoodsList = goodsMapper.findMallGoodsList(pageUtil);
        int total = goodsMapper.getMallGoodsListTotal(pageUtil);
        PageResult pageResult = new PageResult(mallGoodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public int updateBatchSellStatus(Long[] ids, int sellStatus) {
        int result = goodsMapper.updateBatchSellStatus(ids, sellStatus);
        if (result>0){
            try {
                // 第一步：初始化 BulkRequest 来批量处理多个更新操作
                BulkRequest bulkRequest = new BulkRequest();

                // 第二步：遍历传入的 ID 列表，创建每个 ID 的 UpdateRequest
                for (Long id : ids) {
                    // 第三步：为每个 ID 创建一个 UpdateRequest
                    UpdateRequest updateRequest = new UpdateRequest("goods", id.toString());

                    // 第四步：设置要更新的字段（这里是 goodsSellStatus）
                    updateRequest.doc("{\"goodsSellStatus\": " + sellStatus + "}", XContentType.JSON);

                    // 将 UpdateRequest 添加到 BulkRequest 中
                    bulkRequest.add(updateRequest);
                }
                restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

            }catch (IOException e){
                if (!e.getMessage().contains("200 OK")){
                    log.error("更新索引库信息失败，请检查:{}", e.getMessage());
                    throw new MallException("更新索引库信息失败，请检查后台日志");
                }
                log.info("更新成功");
            }
        }

        return result;
    }

    @Override
    public MallGoods getOneById(Long id) {
        MallGoods mallGoods = goodsMapper.getMallGoodById(id);
        return mallGoods;
    }

    @Override
    public GoodsCategory getCategoryById(Long id) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(id);
        return goodsCategory;
    }

    @Override
    public String updateMallGoods(MallGoods goods) {
        // 1. 查询分类
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        // 2. 查询商品是否存在
        if (goodsMapper.getMallGoodById(goods.getGoodsId()) == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        // 3. 查询同一类别下是否有同名商品 这种情况下货物id需一致， 保证无重名
        MallGoods temp = goodsMapper.selectByIdAndName(goods.getGoodsCategoryId(), goods.getGoodsName());
        if (temp != null && !temp.getGoodsId().equals(goods.getGoodsId())) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        // 4. 更新数据
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateMallGoods(goods) > 0){
            try {
                UpdateRequest updateRequest = new UpdateRequest("goods", goods.getGoodsId().toString());
                updateRequest.doc(JSON.toJSONString(goods), XContentType.JSON);

                restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            }catch (IOException e) {
                if (!e.getMessage().contains("200 OK")){
                    log.error("更新索引库信息失败，请检查:{}", e.getMessage());
                    throw new MallException("更新索引库信息失败，请检查后台日志");
                }
                log.info("更新成功");
            }

            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public MallGoodsDetailVO getDetail(Long goodsId) {
        // 获取商品信息
        MallGoods mallGoods = goodsMapper.getMallGoodById(goodsId);
        if (mallGoods == null){
            MallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        if (mallGoods.getGoodsSellStatus() != Constants.SELL_STATUS_UP){
            MallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        MallGoodsDetailVO mallGoodsDetailVO = new MallGoodsDetailVO();
        BeanUtil.copyProperties(mallGoods, mallGoodsDetailVO);
        // 获取轮播图列表
        mallGoodsDetailVO.setGoodsCarouselList(mallGoods.getGoodsCarousel().split(","));

        return mallGoodsDetailVO;
    }

    @Override
    public PageResult searchMallGoods(PageQueryUtil pageQueryUtil) {
        try {
//            System.out.println(pageQueryUtil.get("keyWord"));
            SearchRequest searchRequest = new SearchRequest("goods");

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            if (StringUtils.isEmpty(pageQueryUtil.get("keyWord")) && pageQueryUtil.get("goodsCategoryId") == null) {
                searchRequest.source().query(QueryBuilders.matchAllQuery());
            } else {
                if (!StringUtils.isEmpty(pageQueryUtil.get("keyWord"))) {
                    // 创建一个 bool 查询
                    boolQuery.must(
                            QueryBuilders.boolQuery()
                                    .should(QueryBuilders.matchQuery("goodsName", pageQueryUtil.get("keyWord")))
                                    .should(QueryBuilders.matchQuery("goodsIntro", pageQueryUtil.get("keyWord")))
                    );
                }
                // 添加一个查询条件，匹配商品名称
                boolQuery.should(QueryBuilders.matchQuery("goodsName", pageQueryUtil.get("keyWord")));
                // 添加一个查询条件，匹配商品介绍
                boolQuery.should(QueryBuilders.matchQuery("goodsIntro", pageQueryUtil.get("keyWord")));
                boolQuery.filter(QueryBuilders.termQuery("goodsSellStatus", 0));

                if (pageQueryUtil.get("goodsCategoryId") != null) {
                    boolQuery.must(QueryBuilders.matchQuery("goodsCategoryId", pageQueryUtil.get("goodsCategoryId").toString()));
                }
            }

            // 设置查询条件
            searchRequest.source().query(boolQuery);

            String orderBy = (String) pageQueryUtil.get("orderBy");
            System.out.println(orderBy);
            if (StringUtils.hasText(orderBy)){
                if (orderBy.equals("new")){
                    searchRequest.source().sort("goodsId", SortOrder.DESC);
                }else if (orderBy.equals("price")){
                    searchRequest.source().sort("sellingPrice", SortOrder.ASC);
                }
            }


            // 分页
            int start = (int) pageQueryUtil.get("start");
            int limit = pageQueryUtil.getLimit();
            searchRequest.source().from(start).size(limit);

            // 发送请求
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            // 获取总条数
            int total = (int) hits.getTotalHits().value;
            SearchHit[] searchHits = hits.getHits();
            log.info(Arrays.toString(searchHits));
            List<MallGoods> mallGoodsList = Arrays.stream(searchHits)
                    .map(hit -> JSON.parseObject(hit.getSourceAsString(), MallGoods.class))
                    .collect(Collectors.toList());
            List<MallSearchGoodsVO> mallSearchGoodsVOS = new ArrayList<>();
            if (!CollectionUtils.isEmpty(mallGoodsList)){
                mallSearchGoodsVOS = BeanUtil.copyList(mallGoodsList, MallSearchGoodsVO.class);
                for (MallSearchGoodsVO mallSearchGoodsVO : mallSearchGoodsVOS){
                    if (mallSearchGoodsVO.getGoodsName().length() > 28){
                        mallSearchGoodsVO.setGoodsName(mallSearchGoodsVO.getGoodsName().substring(0, 28)+"...");
                    }
                    if (mallSearchGoodsVO.getGoodsIntro().length() > 30){
                        mallSearchGoodsVO.setGoodsIntro(mallSearchGoodsVO.getGoodsIntro().substring(0, 30)+"...");
                    }
                }
            }

            return new PageResult<>(mallGoodsList, total, start, limit);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



//        List<MallGoods> mallGoodsList = goodsMapper.searchMallGoods(pageQueryUtil);
//        int total = goodsMapper.searchMallGoodsListTotal(pageQueryUtil);
//        List<MallSearchGoodsVO> mallSearchGoodsVOS = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(mallGoodsList)){
//            mallSearchGoodsVOS = BeanUtil.copyList(mallGoodsList, MallSearchGoodsVO.class);
//            for (MallSearchGoodsVO mallSearchGoodsVO : mallSearchGoodsVOS){
//                if (mallSearchGoodsVO.getGoodsName().length() > 28){
//                    mallSearchGoodsVO.setGoodsName(mallSearchGoodsVO.getGoodsName().substring(0, 28)+"...");
//                }
//                if (mallSearchGoodsVO.getGoodsIntro().length() > 30){
//                    mallSearchGoodsVO.setGoodsIntro(mallSearchGoodsVO.getGoodsIntro().substring(0, 30)+"...");
//                }
//            }
//        }
//
//        return new PageResult(mallSearchGoodsVOS, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }

    @Override
    public List<MallGoods> getListByIds(List<Long> goodsIds) {
        return goodsMapper.selectListByIds(goodsIds);
    }

    @Override
    public int deductStockNum(StockNumDTO stockNumDTO) {
        return goodsMapper.deductStockNum(stockNumDTO);
    }

    @Override
    public int recoverStockNum(StockNumDTO stockNumDTO) {
        return goodsMapper.recoverStockNum(stockNumDTO);
    }
}
