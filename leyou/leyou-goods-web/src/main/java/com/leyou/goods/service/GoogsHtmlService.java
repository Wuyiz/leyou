package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author: suhai
 * @create: 2020-10-11 18:16
 * 页面静态化
 */
@Service
public class GoogsHtmlService {
    @Autowired
    private TemplateEngine engine;
    @Autowired
    private GoodsService goodsService;

    public void createHtml(Long supId, Map<String, Object> map) {
        //初始化运行上下文
        Context context = new Context();
        //设置数据模型
        context.setVariables(map);

        PrintWriter printWriter = null;
        try {
            //把静态文件生成到Nginx服务器本地
            File file = new File("E:\\Software\\PHPstudy\\phpstudy_pro\\Extensions\\Nginx1.15.11\\html\\item\\" + supId + ".html");
            printWriter = new PrintWriter(file);

            engine.process("item", context, printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    /**
     * 重载静态页面生成方法，供消息队列更新静态页面数据使用
     * @param supId
     */
    public void createHtml(Long supId) {
        //初始化运行上下文
        Context context = new Context();
        //设置数据模型
        Map<String, Object> map = goodsService.loadData(supId);
        context.setVariables(map);

        PrintWriter printWriter = null;
        try {
            //把静态文件生成到Nginx服务器本地
            File file = new File("E:\\Software\\PHPstudy\\phpstudy_pro\\Extensions\\Nginx1.15.11\\html\\item\\" + supId + ".html");
            printWriter = new PrintWriter(file);

            engine.process("item", context, printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }
}
