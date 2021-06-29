package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author: suhai
 * @create: 2020-10-08 00:27
 */
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandService.class);

    /**
     * 根据前查询条件分页查询品牌数据并排序
     * key:  搜索条件   page:  当前页
     * rows:  每页大小  sortBy:  排序字段   desc:  是否降序
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }
        //添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }
        //查询结果集
        List<Brand> brands = brandMapper.selectByExample(example);
        //添加分页条件
        PageHelper.startPage(page, rows);
        //包装成pageinfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        //封装分页结果集返回
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新增品牌  {name: "123", image: "", cids: "3,5,22", letter: "Q"}
     * @param brand
     * @param cids
     * @return
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增brand
        brandMapper.insertSelective(brand);
        //新增中间表 tb_category_brand
        cids.stream().forEach(cid -> {
            brandMapper.insertCategoryAndBrand(cid, brand.getId());
        });
    }

    /**
     * 根据cid查询所属品牌集合
     * @param cid
     * @return
     */
    public List<Brand> queryBrandsByCid(Long cid) {
        return brandMapper.selectBrandsByCid(cid);
    }

    /**
     * 根据id查询brand
     * @param id
     * @return
     */
    public Brand queryBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
