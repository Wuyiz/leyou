package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: suhai
 * @create: 2020-10-10 06:26
 */
@RequestMapping("spec")
public interface SpecificationApi {
    /**
     * 根据cid查询到规格参数组以及参数信息
     * @param cid
     * @return
     */
    @GetMapping("group/param/{cid}")
    List<SpecGroup> queryGroupsWithParams(@PathVariable("cid") Long cid);

    /**
     * 根据规格参数组id查询此组下的规格参数信息
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    @GetMapping("params")
    List<SpecParam> queryParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "generic", required = false) Boolean generic,
            @RequestParam(value = "searching", required = false) Boolean searching
    );
}
