package com.cs.mapper;

import com.cs.domain.po.GoodsCategory;
import com.cs.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsCategoryMapper {

    GoodsCategory selectByLevelAndName(@Param("categoryLevel") Byte categoryLevel, @Param("categoryName") String categoryName);

    GoodsCategory selectByPrimaryKey(Long goodsCategoryId);

    List<GoodsCategory> getGoodsCategoryList(PageQueryUtil pageUtil);

    int getTotalGoodsCategories(PageQueryUtil pageUtil);

    int insertSelective(GoodsCategory goodsCategory);

    int updateCategory(GoodsCategory category);

    int deleteBatch(Long[] ids);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("parentIds") List<Long> parentIds, @Param("categoryLevel") int categoryLevel, @Param("number") int number);
}
