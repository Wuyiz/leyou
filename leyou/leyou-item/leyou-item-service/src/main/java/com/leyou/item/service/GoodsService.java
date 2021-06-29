package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBO;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: suhai
 * @create: 2020-10-09 20:03
 */
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AmqpTemplate amqpTemplate;


    /**
     *
     * @param key 关键字：模糊查询title，可为空
     * @param saleable 上下架条件：true/false 可以不存在
     * @param page  当前页
     * @param rows  显示行数
     * @return
     */
    public PageResult<SpuBO> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //添加查询条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //添加上下架的过滤条件
        if (saleable != null) {
            criteria.orEqualTo("saleable", saleable);
        }
        //执行查询，获取spu集合
        List<Spu> spus = spuMapper.selectByExample(example);
        //spu集合转化成SpuBO
        List<SpuBO> spuBOs = spus.stream().map(spu -> {
            SpuBO spuBO = new SpuBO();
            //复制实体对象
            BeanUtils.copyProperties(spu, spuBO);
            //查询品牌的名称并保存
            spuBO.setBname(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
            //查询category的名称并保存
            List<String> names = categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBO.setCname(StringUtils.join(names, "/"));
            //返回BO对象
            return spuBO;
        }).collect(Collectors.toList());
        //添加分页
        PageHelper.startPage(page, rows);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        //返回pageResult<SpuBO>
        return new PageResult<>(pageInfo.getTotal(), spuBOs);
    }

    /**
     * 新增商品
     * @param spuBO
     */
    @Transactional
    public void saveGoods(SpuBO spuBO) {
        spuBO.setSaleable(true);
        spuBO.setValid(true);
        spuBO.setCreateTime(new Date());
        spuBO.setLastUpdateTime(spuBO.getCreateTime());
        //增加spu
        spuMapper.insertSelective(spuBO);
        //增加spu_detail
        SpuDetail spuDetail = spuBO.getSpuDetail();
        spuDetail.setSpuId(spuBO.getId());
        spuDetailMapper.insertSelective(spuDetail);
        //增加sku
        saveSkuAndStock(spuBO);
        //发送消息队列
        sendMsg("insert", spuBO.getId());
    }

    private void sendMsg(String type, Long id) {
        //发送消息队列
        try {
            amqpTemplate.convertAndSend("item." + type, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新商品
     * @param spuBO
     * @return
     */
    @Transactional
    public void updateGoods(SpuBO spuBO) {
        //根据spuId查询要删除的sku
        Sku record = new Sku();
        record.setSpuId(spuBO.getId());
        List<Long> ids = skuMapper.select(record).stream().map(Sku::getId).collect(Collectors.toList());
        //删除stock
        ids.forEach(id -> stockMapper.deleteByPrimaryKey(id));
        //删除sku
        skuMapper.delete(record);
        //新增sku
        saveSkuAndStock(spuBO);
        //前端会传来这些字段的信息，不能更新，设置null不写入数据库
        spuBO.setCreateTime(null);
        spuBO.setLastUpdateTime(new Date());
        spuBO.setSaleable(null);
        spuBO.setValid(null);
        //更新spu和spuDetail
        spuMapper.updateByPrimaryKeySelective(spuBO);
        spuDetailMapper.updateByPrimaryKeySelective(spuBO.getSpuDetail());
        //发送消息队列
        sendMsg("update", spuBO.getId());
    }

    private void saveSkuAndStock(SpuBO spuBO) {
        spuBO.getSkus().forEach(sku -> {
            sku.setSpuId(spuBO.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);
            //增加stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        });
    }

    /**
     * 查询spudetail
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据id查询sku
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(record);
        skus.forEach(sku -> {
            Stock stock = stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 根据id查询Spu
     * @param id
     * @return
     */
    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * cart微服务中新增购物车，查询商品详情
     * @param skuId
     * @return
     */
    public Sku querySkuBySkuId(Long skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }
}
