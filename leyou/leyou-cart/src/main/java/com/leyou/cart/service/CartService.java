package com.leyou.cart.service;

import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.popj.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.api.GoodsApi;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: suhai
 * @create: 2020-10-14 01:50
 */
@Service
public class CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodsApi goodsApi;

    private static final String KEY_PREFIX = "user:cart:";

    /**
     * 添加购物车商品信息到redis
     * @param cart
     * @return
     */
    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //查询购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        String key = cart.getSkuId().toString();
        //判断当前的商品是否在购物车中
        if (hashOperations.hasKey(key)) {
            Integer num = cart.getNum();
            //存在，更新数量
            String cartJson = hashOperations.get(key).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum() + num);
        } else {
            //不存在，新增购物车
            Sku sku = goodsApi.querySkuBySkuId(cart.getSkuId());
            cart.setUserId(userInfo.getId());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            String images = sku.getImages();
            cart.setImage(StringUtils.isBlank(images) ? "" : StringUtils.split(images, ",")[0]);
            cart.setPrice(sku.getPrice());
        }
        hashOperations.put(key, JsonUtils.serialize(cart));
    }

    /**
     * 通过配置的拦截器中获取的userid查询所属购物车信息
     * @return
     */
    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String prefix = KEY_PREFIX + userInfo.getId();
        //判断用户是否存在购物车记录
        if (!redisTemplate.hasKey(prefix)) {
            return null;
        }
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(prefix);
        //获取购物车Map中所有的Cart值集合
        List<Object> cartsJson = hashOperations.values();
        //如果购物车集合为空，直接返回null
        if (CollectionUtils.isEmpty(cartsJson)) {
            return null;
        }
        //把List<Object>集合转化为List<Cart>集合
        return cartsJson.stream().map(cartJson -> JsonUtils.parse(cartJson.toString(), Cart.class)).collect(Collectors.toList());
    }

    /**
     * 修改购物车商品数量信息
     * @param cart
     * @return
     */
    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String prefix = KEY_PREFIX + userInfo.getId();
        //判断用户是否存在购物车记录
        if (!redisTemplate.hasKey(prefix)) {
            return;
        }
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(prefix);
        String cartJson = hashOperations.get(cart.getSkuId().toString()).toString();
        Integer num = cart.getNum();
        cart = JsonUtils.parse(cartJson, Cart.class);
        cart.setNum(num);
        hashOperations.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }

    /**
     * 删除购物车中的一条商品
     * @param skuId
     * @return
     */
    public void deleteCart(Long skuId) {
        //获取登录用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String prefix = KEY_PREFIX + userInfo.getId();
        //判断用户是否存在购物车记录
        if (!redisTemplate.hasKey(prefix)) {
            return;
        }
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(prefix);
        //删除
        hashOperations.delete(skuId.toString());
    }
}
