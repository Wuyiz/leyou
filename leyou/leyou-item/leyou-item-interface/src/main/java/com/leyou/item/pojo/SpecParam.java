package com.leyou.item.pojo;

import javax.persistence.*;

/**
 * @author: suhai
 * @create: 2020-10-09 16:14
 * 规格参数组下的参数名
 *
 */
@Table(name = "tb_spec_param")
public class SpecParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid;       //商品分类id
    private Long groupId;   //组id
    private String name;    //参数名
    //由于numeric在mysql中是关键字，需要加``
    @Column(name = "`numeric`")
    private Boolean numeric;    //是否是数字类型参数
    private String unit;        //数字类型参数的单位，非数字类型可以为空
    private Boolean generic;    //是否是sku通用属性
    private Boolean searching;  //是否用于搜索过滤
    private String segments;    //数值类型参数，如果需要搜索，则添加分段间隔值，如CPU频率间隔：0.5-1.0

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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNumeric() {
        return numeric;
    }

    public void setNumeric(Boolean numeric) {
        this.numeric = numeric;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getGeneric() {
        return generic;
    }

    public void setGeneric(Boolean generic) {
        this.generic = generic;
    }

    public Boolean getSearching() {
        return searching;
    }

    public void setSearching(Boolean searching) {
        this.searching = searching;
    }

    public String getSegments() {
        return segments;
    }

    public void setSegments(String segments) {
        this.segments = segments;
    }
}
