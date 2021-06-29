package com.leyou.goods.controller;

import com.leyou.goods.service.GoodsService;
import com.leyou.goods.service.GoogsHtmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author: suhai
 * @create: 2020-10-10 05:57
 */
@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoogsHtmlService googsHtmlService;

    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model) {
        Map<String, Object> map = goodsService.loadData(id);
        model.addAllAttributes(map);
        //页面静态化
        googsHtmlService.createHtml(id, map);
        return "item";
    }
}
