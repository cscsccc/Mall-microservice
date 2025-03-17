package com.cs.controller.mall;

import com.cs.common.ServiceResultEnum;
import com.cs.domain.vo.MallIndexCategoryVO;
import com.cs.service.MallCategoryService;
import com.cs.util.Result;
import com.cs.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MallGoodsCategoryAPI {
    @Autowired
    private MallCategoryService mallCategoryService;

    @GetMapping("/categories")
    public Result getCategories(){
        List<MallIndexCategoryVO> mallIndexCategoryVOS = mallCategoryService.getCategoriesForIndex();
        System.out.println(mallIndexCategoryVOS);

        if (!CollectionUtils.isEmpty(mallIndexCategoryVOS)){
            return ResultGenerator.genSuccessResult(mallIndexCategoryVOS);
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }
}
