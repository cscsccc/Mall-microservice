package com.cs.service;

import com.cs.entity.po.Carousels;
import com.cs.entity.vo.MallIndexCarouselVO;
import com.cs.util.PageQueryUtil;
import com.cs.util.PageResult;

import java.util.List;

public interface CarouselService {

    PageResult getCarousels(PageQueryUtil pageQueryUtil);

    String insertCarousel(Carousels carousel);

    String updateByBatchId(Long[] ids);

    List<MallIndexCarouselVO> selectCarouselList(int indexCarouselNumber);
}
