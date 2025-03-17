package com.cs.service;

import com.cs.domain.po.GoodsCategory;
import com.cs.domain.vo.MallIndexCategoryVO;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;

import java.util.List;

public interface MallCategoryService {

    PageResult getCategories(PageQueryUtil pageUtil);

    String saveCategory(GoodsCategory goodsCategory);

    GoodsCategory selectByPrimaryKey(Long id);

    String updateCategory(GoodsCategory category);

    boolean deleteBatch(Long[] ids);

    List<MallIndexCategoryVO> getCategoriesForIndex();
}


