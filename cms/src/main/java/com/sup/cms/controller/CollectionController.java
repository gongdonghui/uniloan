package com.sup.cms.controller;

import com.sup.cms.bean.vo.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: kouichi
 * @Date: 2019/9/24 15:50
 */
@RequestMapping("/collection")
@RestController
public class CollectionController {
//todo 逾期派单
// ->查询关老师operation表，把逾期的记录显示出来
// ->管理员进行派单操作
// ->根据applyId找到operation表中对应的记录 update 指派人 被指派人
// ->打开我的催收页面
// ->显示所有指派给自己的单子
// ->找到要催收的单子，点详情，拉到最下面可一条一条的添加 催收记录 这就完成了整个催收流程
// ->催收档案里显示所有已经指派了人的催收情况 在这里可以重新指派之类的

    /**
     * 查看指派记录按钮
     *
     * @param applyId
     * @return
     */
    @GetMapping("/allocateRecords")
    public String allocateRecords(@RequestParam("") String applyId) {
        return "";
    }

    /**
     * 添加催收记录按钮
     * @param params
     * @return
     */
    @PostMapping("/addAllocateRecord")
    public String addAllocateRecord(@Valid @RequestBody CollectionAddAllocateRecordParams params) {
        return "";
    }

    /**
     * 预期派单-列表
     * @param params
     * @return
     */
    @PostMapping("/allocate/getList")
    public String allocateGetList(@Valid @RequestBody CollectionAllocateGetListParams params) {
        return "";
    }

    /**
     * 预期派单-派单按钮
     * @param params
     * @return
     */
    @PostMapping("/allocate/action")
    public String allocateAction(@Valid @RequestBody CollectionAllocateActionParams params) {
        //todo 不仅要update表 还要找地方存指派记录
        return "";
    }

    /**
     * 我的催收-列表
     * @param params
     * @return
     */
    @PostMapping("/mine/getList")
    public String mine(@Valid @RequestBody CollectionMineGetListParams params) {
        return "";
    }

    /**
     * 催收档案-列表
     * @param params
     * @return
     */
    @PostMapping("/archives/getList")
    public String archivesGetList(@Valid @RequestBody CollectionArchivesGetListParams params) {
        return "";
    }

    /**
     * 催收档案-重新指派按钮
     * @param params
     * @return
     */
    @PostMapping("/archives/reAllocate")
    public String reAllocate(@Valid @RequestBody CollectionArchivesReAllocateParams params) {
        return "";
    }

    //todo 还差一个召回按钮 不知道干啥的 待商讨

}
