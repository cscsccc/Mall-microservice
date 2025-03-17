package com.cs.service.impl;

import com.cs.client.GoodsClient;
import com.cs.common.Constants;
import com.cs.common.MallException;
import com.cs.common.ServiceResultEnum;
import com.cs.dto.MallGoods;
import com.cs.entity.po.MallSeckill;
import com.cs.entity.po.MallSeckillSuccess;
import com.cs.entity.vo.MallSeckillGoodsVO;
import com.cs.entity.vo.SeckillSuccessVO;
import com.cs.mapper.*;
import com.cs.redis.RedisCache;
import com.cs.service.MallSecKillService;
import com.cs.util.BeanUtil;
import com.cs.util.MD5Util;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MallSecKillServiceImpl implements MallSecKillService {
    // 使用令牌桶RateLimiter 限流
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(100);

    @Autowired
    private MallSecKillMapper mallSecKillMapper;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private MallSeckillSuccessMapper mallSeckillSuccessMapper;
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageResult findSecKillList(PageQueryUtil pageUtil) {

        List<MallSeckillGoodsVO> mallSeckillGoodsVOS = redisCache.getCacheObject(Constants.SECKILL_GOODS_LIST);

        if (mallSeckillGoodsVOS == null){
            List<MallSeckill> mallSeckills = mallSecKillMapper.findSecKillList();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            mallSeckillGoodsVOS = mallSeckills.stream().map(mallSeckill ->{
                MallSeckillGoodsVO mallSeckillGoodsVO = new MallSeckillGoodsVO();
                BeanUtil.copyProperties(mallSeckill,mallSeckillGoodsVO);
                MallGoods mallGoods = goodsClient.getOneById(mallSeckill.getGoodsId());
                if (mallGoods == null || mallGoods.getGoodsSellStatus().intValue() == 1 || mallGoods.getStockNum() <= 0){
                    return null;
                }
                mallSeckillGoodsVO.setGoodsCoverImg(mallGoods.getGoodsCoverImg());
                mallSeckillGoodsVO.setGoodsIntro(mallGoods.getGoodsIntro());
                mallSeckillGoodsVO.setGoodsName(mallGoods.getGoodsName());
                Date seckillBegin = mallSeckillGoodsVO.getSeckillBegin();
                Date seckillEnd = mallSeckillGoodsVO.getSeckillEnd();
                String formatBegin = sdf.format(seckillBegin);
                String formatEnd = sdf.format(seckillEnd);
                mallSeckillGoodsVO.setSeckillBeginTime(formatBegin);
                mallSeckillGoodsVO.setSeckillEndTime(formatEnd);
                return mallSeckillGoodsVO;
            }).filter(Objects::nonNull).toList();
            redisCache.setCacheObject(Constants.SECKILL_GOODS_LIST, mallSeckillGoodsVOS, 60 * 60 * 100, TimeUnit.SECONDS);
        }
        int start = (pageUtil.getPage() - 1) * pageUtil.getLimit();
        int end = Math.min(start + pageUtil.getLimit(), mallSeckillGoodsVOS.size());
        System.out.println(mallSeckillGoodsVOS);
        List<MallSeckillGoodsVO> paginatedList = mallSeckillGoodsVOS.subList(start, end);

        int total = mallSecKillMapper.findSecKillListTotal(pageUtil);

        return new PageResult(paginatedList, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public PageResult findSecKillListForAdminPage(PageQueryUtil pageUtil) {
        List<MallSeckill> mallSeckills = mallSecKillMapper.findSecKillListForAdmin(pageUtil);
        int total = mallSecKillMapper.findSecKillListTotal(pageUtil);

        return new PageResult(mallSeckills, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public MallSeckill findSecKillByPrimaryKey(Long seckillId) {
        return mallSecKillMapper.findSeckillByPrimaryKey(seckillId);
    }

    @Override
    public boolean updateSeckill(MallSeckill mallSeckill) {
        // 判断商品是否存在
        MallGoods mallGoods = goodsClient.getOneById(mallSeckill.getGoodsId());
        if (mallGoods == null) {
            MallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        // 判断商品是否已在秒杀里
        MallSeckill mallSeckill1 = mallSecKillMapper.findSecKillByGoodsId(mallSeckill.getGoodsId());

        if (mallSeckill1 != null && !mallSeckill1.getSeckillId().equals(mallSeckill.getSeckillId())) {
            MallException.fail(ServiceResultEnum.SAME_GOODS_EXIST.getResult());
        }
        // 判断秒杀存在
        MallSeckill temp = mallSecKillMapper.findSeckillByPrimaryKey(mallSeckill.getSeckillId());
        if (temp == null) {
            MallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }

        mallSeckill.setUpdateTime(new Date());

        return mallSecKillMapper.updateSecKillBySelective(mallSeckill) > 0;
    }

    @Override
    public boolean deleteSeckillByPrimaryKeys(Long[] ids) {
        return mallSecKillMapper.deleteSeckillByPrimaryKeys(ids) > 0;
    }

    @Override
    public boolean saveSeckill(MallSeckill mallSeckill) {
        MallGoods mallGoods = goodsClient.getOneById(mallSeckill.getGoodsId());
        if (mallGoods == null) {
            MallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        MallSeckill mallSeckill1 = mallSecKillMapper.findSecKillByGoodsId(mallSeckill.getGoodsId());
        if (mallSeckill1 != null) {
            MallException.fail(ServiceResultEnum.SAME_GOODS_EXIST.getResult());
        }
        mallSeckill.setCreateTime(new Date());
        mallSeckill.setUpdateTime(new Date());

        return mallSecKillMapper.saveSeckill(mallSeckill) > 0;
    }

    @Override
    public SeckillSuccessVO executeSeckill(Long seckillId, Long userId) {
        if (!RATE_LIMITER.tryAcquire(500, TimeUnit.MILLISECONDS)) {
            MallException.fail("秒杀失败");
        }
        //判断是否购买过
        if (redisCache.containsCacheSet(Constants.SECKILL_SUCCESS_USER_ID + seckillId, userId)){
            MallException.fail("已购买,请勿重复购买");
        }

        Long stock = redisCache.luaDecrement(Constants.SECKILL_GOODS_STOCK_KEY + seckillId);
        if (stock == null || stock < 0){
            MallException.fail("已售空");
        }

        //获取秒杀商品信息
        MallSeckill mallSeckill = redisCache.getCacheObject(Constants.SECKILL_KEY + seckillId);

        if (mallSeckill == null) {
            mallSeckill = mallSecKillMapper.findSeckillByPrimaryKey(seckillId);
            redisCache.setCacheObject(Constants.SECKILL_KEY + seckillId, mallSeckill);
        }

        long beginTime = mallSeckill.getSeckillBegin().getTime();
        long endTime = mallSeckill.getSeckillEnd().getTime();
        long now = new Date().getTime();
        if (now < beginTime){
            MallException.fail("秒杀未开始");
        }
        if (now > endTime){
            MallException.fail("秒杀已结束");
        }

        Date killTime = new Date();
        Map params = new HashMap(8);
        params.put("seckillId", seckillId);
        params.put("userId", userId);
        params.put("killTime", killTime);
        params.put("result", null);

        // 执行存储过程 并返回到result
        try {
            mallSecKillMapper.killByProcedure(params);
        } catch (Exception e){
            MallException.fail(e.getMessage());
        }

        params.get("result");
        int result = MapUtils.getInteger(params, "result", -2);
        if (result != 1){
            MallException.fail("很遗憾，未抢到");
        }
        redisCache.setCacheSet(Constants.SECKILL_SUCCESS_USER_ID+seckillId, userId);
        long endExpireTime = endTime / 1000;
        long nowExpireTime = now / 1000;
        redisCache.expire(Constants.SECKILL_SUCCESS_USER_ID + seckillId, endExpireTime - nowExpireTime, TimeUnit.SECONDS);
        MallSeckillSuccess mallSeckillSuccess = mallSeckillSuccessMapper.findByUserIdAndSeckillId(userId, seckillId);

        SeckillSuccessVO seckillSuccessVO = new SeckillSuccessVO();
        Long seckillSuccessId = mallSeckillSuccess.getSecId();
        seckillSuccessVO.setSeckillSuccessId(seckillSuccessId);
        seckillSuccessVO.setMd5(MD5Util.MD5Encode(seckillSuccessId + Constants.SECKILL_ORDER_SALT, "UTF-8"));

        Map<String, Long> createOrderMap = new HashMap();
        createOrderMap.put("seckillId", seckillId);
        createOrderMap.put("userId", userId);

        rabbitTemplate.convertAndSend("seckill.direct", "createOrder", createOrderMap);
        return seckillSuccessVO;
    }
}
