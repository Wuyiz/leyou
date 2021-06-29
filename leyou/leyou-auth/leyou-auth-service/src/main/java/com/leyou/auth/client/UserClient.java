package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: suhai
 * @create: 2020-10-13 14:23
 */
@FeignClient("user-service")
public interface UserClient extends UserApi {
}
