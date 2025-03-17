package com.cs.service;

import com.cs.entity.po.IndexConfig;
import com.cs.entity.vo.MallIndexConfigGoodsVO;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;

import java.util.List;

public interface IndexConfigService {
    PageResult getIndexConfigPage(PageQueryUtil pageQueryUtil);

    String addIndexConfig(IndexConfig indexConfig);

    IndexConfig selectIndexConfigById(Long id);

    String updateIndexConfig(IndexConfig indexConfig);

    int deleteIndexConfig(Long[] ids);

    List<MallIndexConfigGoodsVO> selectIndexConfigForIndex(int type, int number);
}
