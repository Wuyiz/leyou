package com.leyou.item.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: suhai
 * @create: 2020-10-10 06:26
 */
@RequestMapping("category")
public interface CategoryApi {
    @GetMapping
    List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);
}

