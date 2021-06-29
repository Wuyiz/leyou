package com.leyou.item.pojo;

import javax.persistence.*;
import java.util.List;

/**
 * @author: suhai
 * @create: 2020-10-09 16:12
 * 规格参数的分组表，每个商品分类下有多个规格参数组
 */
@Table(name = "tb_spec_group")
public class SpecGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        //组id
    private Long cid;       //分类id
    private String name;    //组名

    //参数组表中不存在这个字段，此处作为临时变量存储规格参数
    @Transient
    private List<SpecParam> params;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SpecParam> getParams() {
        return params;
    }

    public void setParams(List<SpecParam> params) {
        this.params = params;
    }
}
