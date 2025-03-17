package com.cs.controller.mall;

import com.cs.common.Constants;
import com.cs.config.annotation.TokenToMallUser;
import com.cs.domain.po.MallGoods;
import com.cs.dto.StockNumDTO;
import com.cs.pojo.MallUserToken;
import com.cs.service.MallGoodsService;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import io.seata.core.context.RootContext;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class MallGoodsAPI {

    @Autowired
    private MallGoodsService mallGoodsService;

    @RequestMapping(value = "/goods/detail/{goodsId}", method = RequestMethod.GET)
    public Result getDetail(@PathVariable("goodsId") Long goodsId) {
        if (goodsId < 1){
            return ResultGenerator.genFailResult("参数异常");
        }
        return ResultGenerator.genSuccessResult(mallGoodsService.getDetail(goodsId));
    }

    @GetMapping("/search")
    public Result search(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) Long goodsCategoryId,
                         @RequestParam(required = false) String orderBy,
                         @RequestParam(required = false) Integer pageNumber,
                         @TokenToMallUser MallUserToken mallUserToken) {

        Map params = new HashMap();
        params.put("page", pageNumber);
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        params.put("goodsCategoryId", goodsCategoryId);
        if (StringUtils.hasText(keyword)) {
            params.put("keyWord", keyword);
        }
        if (StringUtils.hasText(orderBy)) {
            params.put("orderBy", orderBy);
        }
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);

        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult result = mallGoodsService.searchMallGoods(pageQueryUtil);

        return ResultGenerator.genSuccessResult(result);
    }

    @GetMapping("/getListById")
    public List<MallGoods> getListByIds(@RequestParam("goodsIds") List<Long> goodsIds) {
        return mallGoodsService.getListByIds(goodsIds);
    }

    @GetMapping("/getMallGoodById")
    public MallGoods getListByIds(@RequestParam("goodsId") Long goodsId) {
        return mallGoodsService.getOneById(goodsId);
    }

    @PostMapping("/deductStockNum")
    public int deductStockNum(@RequestBody StockNumDTO stockNumDTO){
        return mallGoodsService.deductStockNum(stockNumDTO);
    }

    @PostMapping("/recoverStockNum")
    public int recoverStockNum(@RequestBody StockNumDTO stockNumDTO){
        return mallGoodsService.recoverStockNum(stockNumDTO);
    }



}
