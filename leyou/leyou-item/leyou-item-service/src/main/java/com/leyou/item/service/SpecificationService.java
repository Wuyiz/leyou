package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: suhai
 * @create: 2020-10-09 16:28
 */
@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id查询规格参数组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        return specGroupMapper.select(record);
    }

    /**
     * 根据组id更新组名
     * @param specGroup
     * @return
     */
    public void updateGroup(SpecGroup specGroup) {
        specGroupMapper.updateByPrimaryKeySelective(specGroup);
    }

    /**
     * 增加一个组信息
     * @param specGroup
     * @return
     */
    public void saveGroup(SpecGroup specGroup) {
        specGroupMapper.insertSelective(specGroup);
    }

    /**
     * 删除一个组信息
     * @param id
     * @return
     */
    public void deleteGroup(Long id) {
        specGroupMapper.deleteByPrimaryKey(id);
    }


    // =========================================分割线==============================================


    /**
     * 根据规格参数组id查询此组下的规格参数信息
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setGeneric(generic);
        record.setSearching(searching);
        return specParamMapper.select(record);
    }

    /**
     * 根据规格参数id更新数据
     * @param specParam
     * @return
     */
    public void updateParam(SpecParam specParam) {
        specParamMapper.updateByPrimaryKeySelective(specParam);
    }

    /**
     * 增加一个规格参数信息
     * @param specParam
     * @return
     */
    public void saveParam(SpecParam specParam) {
        specParamMapper.insertSelective(specParam);
    }

    /**
     * 删除一个规格参数信息
     * @param id
     * @return
     */
    public void deleteParam(Long id) {
        specParamMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据cid查询到规格参数组以及参数信息
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsWithParams(Long cid) {
        List<SpecGroup> groups = queryGroupsByCid(cid);
        groups.forEach(group -> group.setParams(
                queryParams(group.getId(), null, null, null)
        ));
        return groups;
    }
}
