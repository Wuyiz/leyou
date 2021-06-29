package com.leyou.cart.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: suhai
 * @create: 2020-10-14 02:45
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
