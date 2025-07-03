package com.knowledge.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.knowledge.springboot.common.Statusful;
import com.knowledge.springboot.common.error.user.UserInputParamError;
import com.knowledge.springboot.common.error.user.UserLoginError;
import com.knowledge.springboot.common.util.ValidatorUtils;
import com.knowledge.springboot.domain.KnowledgeTree;
import com.knowledge.springboot.dto.AssociateNotesRequest;
import com.knowledge.springboot.dto.AssociateNotesResponse;
import com.knowledge.springboot.dto.KnowledgeTreeDataResponse;
import com.knowledge.springboot.service.impl.KnowledgeTreeService;
import com.knowledge.springboot.utils.RequestContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 知识树控制器
 */
@RestController
@RequestMapping("/api/knowledge-tree")
@Slf4j
public class KnowledgeTreeController {

    @Resource
    private KnowledgeTreeService knowledgeTreeService;

    @Autowired
    private RequestContextUtil requestContextUtil;

    /**
     * 获取用户的知识树列表
     * @return 知识树列表
     */
    @GetMapping("/list")
    public Object getUserKnowledgeTrees() {
        log.info("获取用户知识树列表");

        String userId = requestContextUtil.getCurrentUserId();
        if (userId == null) {
            return Statusful.error(new UserLoginError());
        }

        try {
            List<KnowledgeTree> trees = knowledgeTreeService.getUserKnowledgeTrees(userId);

            if (trees == null || trees.size() == 0) {
                CreateKnowledgeTreeRequest request = new CreateKnowledgeTreeRequest();
                request.setName("知识树");
                request.setDescription("这里是知识树描述");
                createKnowledgeTree(request);
                trees = knowledgeTreeService.getUserKnowledgeTrees(userId);
            }

            // 遍历trees，取treeId set到 treeIdStr
            for (KnowledgeTree tree : trees) {
                tree.setTreeIdStr(String.valueOf(tree.getTreeId()));
            }
            System.out.println(JSON.toJSONString(trees));
            return Statusful.ok(trees);
        } catch (Exception e) {
            log.error("获取知识树列表失败", e);
            return Statusful.error(new UserInputParamError("获取知识树列表失败：" + e.getMessage()));
        }
    }

    /**
     * 创建新的知识树
     * @param request 创建请求
     * @return 创建的知识树
     */
    @PostMapping("/create")
    public Object createKnowledgeTree(@RequestBody CreateKnowledgeTreeRequest request) {
        log.info("创建知识树，名称：{}", request.getName());

        // 参数验证
        ValidatorUtils.validateEntity(request);

        String userId = requestContextUtil.getCurrentUserId();
        if (userId == null) {
            return Statusful.error(new UserLoginError());
        }

        try {
            KnowledgeTree tree = knowledgeTreeService.createKnowledgeTree(
                userId, request.getName(), request.getDescription());
            return Statusful.ok(tree);
        } catch (Exception e) {
            log.error("创建知识树失败", e);
            return Statusful.error(new UserInputParamError("创建知识树失败：" + e.getMessage()));
        }
    }

    /**
     * 删除知识树
     * @param treeId 知识树ID
     * @return 删除结果
     */
    @DeleteMapping("/{treeId}")
    public Object deleteKnowledgeTree(@PathVariable Long treeId) {
        log.info("删除知识树，ID：{}", treeId);

        String userId = requestContextUtil.getCurrentUserId();
        if (userId == null) {
            return Statusful.error(new UserLoginError());
        }

        try {
            knowledgeTreeService.deleteKnowledgeTree(userId, treeId);
            return Statusful.ok("知识树删除成功");
        } catch (Exception e) {
            log.error("删除知识树失败", e);
            return Statusful.error(new UserInputParamError("删除知识树失败：" + e.getMessage()));
        }
    }

    /**
     * 获取知识树详细数据
     * @param treeId 知识树ID
     * @param includeNoteInfo 是否包含笔记信息
     * @param maxDepth 最大深度
     * @return 知识树数据
     */
    @GetMapping("/{treeId}")
    public Object getKnowledgeTreeData(
            @PathVariable Long treeId,
            @RequestParam(required = false, defaultValue = "false") Boolean includeNoteInfo,
            @RequestParam(required = false) Integer maxDepth) {
        log.info("获取知识树数据，ID：{}，包含笔记信息：{}，最大深度：{}", treeId, includeNoteInfo, maxDepth);

        String userId = requestContextUtil.getCurrentUserId();
        if (userId == null) {
            return Statusful.error(new UserLoginError());
        }

        try {

            KnowledgeTreeDataResponse response = knowledgeTreeService.getUpdatedTreeData(
                userId, treeId, includeNoteInfo, 4);

            // 暂时返回Mock数据，包含15个节点的Java学习知识树
//            KnowledgeTreeDataResponse response = createMockKnowledgeTreeData(treeId, includeNoteInfo, maxDepth);
            return Statusful.ok(response);
        } catch (Exception e) {
            log.error("获取知识树数据失败", e);
            return Statusful.error(new UserInputParamError("获取知识树数据失败：" + e.getMessage()));
        }
    }

    /**
     * 创建Mock知识树数据（15个节点）
     */
    private KnowledgeTreeDataResponse createMockKnowledgeTreeData(Long treeId, Boolean includeNoteInfo, Integer maxDepth) {
        KnowledgeTreeDataResponse response = new KnowledgeTreeDataResponse();
        response.setTreeId(treeId);
        response.setName("Java学习知识树");
        response.setLastUpdateTime("2025-05-24 16:30:00");
        response.setTotalNodes(15);
        response.setAssociatedNotesCount(8);

        // 创建根节点
        KnowledgeTreeDataResponse.TreeNode rootNode = new KnowledgeTreeDataResponse.TreeNode();
        rootNode.setNodeId("root_" + treeId);
        rootNode.setName("Java学习知识树");
        rootNode.setLevel(0);
        rootNode.setProgress(65);
        rootNode.setDescription("Java编程语言完整学习路径");
        rootNode.setKeywords(Arrays.asList("Java", "编程", "学习"));
        rootNode.setAssociatedNotesCount(1);
        rootNode.setIsAutoGenerated(false);
        rootNode.setSourceNoteIds(Arrays.asList());

        if (includeNoteInfo != null && includeNoteInfo) {
            KnowledgeTreeDataResponse.AssociatedNoteInfo noteInfo = new KnowledgeTreeDataResponse.AssociatedNoteInfo();
            noteInfo.setNoteId("1001");
            noteInfo.setTitle("Java学习计划");
            noteInfo.setRelevanceScore(0.95);
            noteInfo.setAssociatedKnowledgePoints(Arrays.asList("Java概述", "学习路径"));
            rootNode.setAssociatedNotes(Arrays.asList(noteInfo));
        }

        // 创建子节点
        List<KnowledgeTreeDataResponse.TreeNode> children = new ArrayList<>();

        // 第一层：Java基础
        KnowledgeTreeDataResponse.TreeNode javaBasic = createTreeNode("node_java_basic", "Java基础", 1, 80,
            "Java基础语法和核心概念", Arrays.asList("语法", "基础", "入门"), 3, true, Arrays.asList("1002", "1003"), includeNoteInfo);

        // Java基础的子节点
        List<KnowledgeTreeDataResponse.TreeNode> basicChildren = new ArrayList<>();
        basicChildren.add(createTreeNode("node_java_syntax", "Java语法", 2, 90,
            "Java基础语法规则", Arrays.asList("语法", "变量", "数据类型"), 2, true, Arrays.asList("1002"), includeNoteInfo));
        basicChildren.add(createTreeNode("node_java_oop", "面向对象编程", 2, 75,
            "Java面向对象编程概念", Arrays.asList("面向对象", "类", "对象", "继承"), 1, true, Arrays.asList("1003"), includeNoteInfo));
        basicChildren.add(createTreeNode("node_java_exception", "异常处理", 2, 70,
            "Java异常处理机制", Arrays.asList("异常", "try-catch", "finally"), 1, true, Arrays.asList("1004"), includeNoteInfo));

        javaBasic.setChildren(basicChildren);
        children.add(javaBasic);

        // 第一层：Java进阶
        KnowledgeTreeDataResponse.TreeNode javaAdvanced = createTreeNode("node_java_advanced", "Java进阶", 1, 60,
            "Java高级特性和应用", Arrays.asList("进阶", "高级", "特性"), 2, true, Arrays.asList("1005"), includeNoteInfo);

        // Java进阶的子节点
        List<KnowledgeTreeDataResponse.TreeNode> advancedChildren = new ArrayList<>();
        advancedChildren.add(createTreeNode("node_java_collection", "集合框架", 2, 65,
            "Java集合框架详解", Arrays.asList("集合", "List", "Set", "Map"), 1, true, Arrays.asList("1005"), includeNoteInfo));
        advancedChildren.add(createTreeNode("node_java_io", "IO流", 2, 55,
            "Java输入输出流操作", Arrays.asList("IO", "流", "文件操作"), 1, true, Arrays.asList("1006"), includeNoteInfo));
        advancedChildren.add(createTreeNode("node_java_thread", "多线程", 2, 50,
            "Java多线程编程", Arrays.asList("多线程", "并发", "同步"), 0, true, Arrays.asList(), includeNoteInfo));

        javaAdvanced.setChildren(advancedChildren);
        children.add(javaAdvanced);

        // 第一层：Java框架
        KnowledgeTreeDataResponse.TreeNode javaFramework = createTreeNode("node_java_framework", "Java框架", 1, 40,
            "Java主流框架学习", Arrays.asList("框架", "Spring", "MyBatis"), 1, true, Arrays.asList("1007"), includeNoteInfo);

        // Java框架的子节点
        List<KnowledgeTreeDataResponse.TreeNode> frameworkChildren = new ArrayList<>();
        frameworkChildren.add(createTreeNode("node_spring", "Spring框架", 2, 45,
            "Spring核心框架", Arrays.asList("Spring", "IOC", "AOP"), 1, true, Arrays.asList("1007"), includeNoteInfo));
        frameworkChildren.add(createTreeNode("node_springboot", "Spring Boot", 2, 35,
            "Spring Boot快速开发", Arrays.asList("SpringBoot", "自动配置", "微服务"), 0, true, Arrays.asList(), includeNoteInfo));
        frameworkChildren.add(createTreeNode("node_mybatis", "MyBatis", 2, 40,
            "MyBatis持久层框架", Arrays.asList("MyBatis", "ORM", "数据库"), 1, true, Arrays.asList("1008"), includeNoteInfo));

        javaFramework.setChildren(frameworkChildren);
        children.add(javaFramework);

        // 第一层：项目实战
        KnowledgeTreeDataResponse.TreeNode javaProject = createTreeNode("node_java_project", "项目实战", 1, 30,
            "Java项目实战经验", Arrays.asList("项目", "实战", "应用"), 0, true, Arrays.asList(), includeNoteInfo);

        children.add(javaProject);

        // 根据maxDepth限制返回的层级
        if (maxDepth != null && maxDepth <= 1) {
            // 只返回第一层，清空子节点的children
            for (KnowledgeTreeDataResponse.TreeNode child : children) {
                child.setChildren(new ArrayList<>());
            }
        } else if (maxDepth != null && maxDepth <= 0) {
            // 只返回根节点
            children.clear();
        }

        rootNode.setChildren(children);
        response.setTree(rootNode);

        return response;
    }

    /**
     * 创建树节点的辅助方法
     */
    private KnowledgeTreeDataResponse.TreeNode createTreeNode(String nodeId, String name, int level, int progress,
            String description, List<String> keywords, int associatedNotesCount, boolean isAutoGenerated,
            List<String> sourceNoteIds, Boolean includeNoteInfo) {

        KnowledgeTreeDataResponse.TreeNode node = new KnowledgeTreeDataResponse.TreeNode();
        node.setNodeId(nodeId);
        node.setName(name);
        node.setLevel(level);
        node.setProgress(progress);
        node.setDescription(description);
        node.setKeywords(keywords);
        node.setAssociatedNotesCount(associatedNotesCount);
        node.setIsAutoGenerated(isAutoGenerated);
        node.setSourceNoteIds(sourceNoteIds);

        // 如果需要包含笔记信息且有关联笔记
        if (includeNoteInfo != null && includeNoteInfo && associatedNotesCount > 0 && !sourceNoteIds.isEmpty()) {
            List<KnowledgeTreeDataResponse.AssociatedNoteInfo> noteInfos = new ArrayList<>();
            for (String noteId : sourceNoteIds) {
                KnowledgeTreeDataResponse.AssociatedNoteInfo noteInfo = new KnowledgeTreeDataResponse.AssociatedNoteInfo();
                noteInfo.setNoteId(noteId);
                noteInfo.setTitle("笔记标题 " + noteId);
                noteInfo.setRelevanceScore(0.85 + 100 * 0.01); // 模拟相关性评分
//                noteInfo.setRelevanceScore(0.85 + (noteId % 10) * 0.01); // 模拟相关性评分
                noteInfo.setAssociatedKnowledgePoints(Arrays.asList(keywords.get(0), keywords.size() > 1 ? keywords.get(1) : "其他"));
                noteInfos.add(noteInfo);
            }
            node.setAssociatedNotes(noteInfos);
        }

        return node;
    }

    /**
     * 关联笔记到知识树
     * @param request 关联请求
     * @return 关联结果
     */
    @PostMapping("/associate-notes")
    public Object associateNotesToTree(@RequestBody AssociateNotesRequest request) {
        log.info("关联笔记到知识树，AssociateNotesRequest：{}", JSON.toJSONString(request));

        // 参数验证
        ValidatorUtils.validateEntity(request);

        String userId = requestContextUtil.getCurrentUserId();
        if (userId == null) {
            return Statusful.error(new UserLoginError());
        }

        // 设置用户ID
        request.setUserId(userId);

        try {
            AssociateNotesResponse response = knowledgeTreeService.associateNotesToTree(request);
            return Statusful.ok(response);
        } catch (Exception e) {
            log.error("关联笔记到知识树失败", e);
            return Statusful.error(new UserInputParamError("关联笔记失败：" + e.getMessage()));
        }
    }

    /**
     * 获取知识树统计信息
     * @param treeId 知识树ID
     * @return 统计信息
     */
    @GetMapping("/{treeId}/statistics")
    public Object getKnowledgeTreeStatistics(@PathVariable Long treeId) {
        log.info("获取知识树统计信息，ID：{}", treeId);

        String userId = requestContextUtil.getCurrentUserId();
        if (userId == null) {
            return Statusful.error(new UserLoginError());
        }

        try {
            // 返回Mock统计数据
            KnowledgeTreeStatistics statistics = new KnowledgeTreeStatistics();
            statistics.setTreeId(treeId);
            statistics.setName("Java学习知识树");
            statistics.setTotalNodes(15);
            statistics.setAssociatedNotesCount(8);
            statistics.setLastUpdateTime("2025-05-24 16:30:00");

            return Statusful.ok(statistics);
        } catch (Exception e) {
            log.error("获取知识树统计信息失败", e);
            return Statusful.error(new UserInputParamError("获取统计信息失败：" + e.getMessage()));
        }
    }

    /**
     * 搜索知识树节点
     * @param treeId 知识树ID
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/{treeId}/search")
    public Object searchKnowledgeNodes(
            @PathVariable Long treeId,
            @RequestParam String keyword) {
        log.info("搜索知识树节点，树ID：{}，关键词：{}", treeId, keyword);

        String userId = requestContextUtil.getCurrentUserId();
        if (userId == null) {
            return Statusful.error(new UserLoginError());
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return Statusful.error(new UserInputParamError("搜索关键词不能为空"));
        }

        try {
            // 获取完整的知识树数据
            KnowledgeTreeDataResponse response = createMockKnowledgeTreeData(treeId, true, null);

            // 搜索匹配的节点
            List<KnowledgeTreeDataResponse.TreeNode> searchResults = searchNodesRecursively(
                response.getTree(), keyword.toLowerCase());

            return Statusful.ok(searchResults);
        } catch (Exception e) {
            log.error("搜索知识树节点失败", e);
            return Statusful.error(new UserInputParamError("搜索失败：" + e.getMessage()));
        }
    }

    /**
     * 递归搜索节点
     */
    private List<KnowledgeTreeDataResponse.TreeNode> searchNodesRecursively(
            KnowledgeTreeDataResponse.TreeNode node, String keyword) {
        List<KnowledgeTreeDataResponse.TreeNode> results = new ArrayList<>();

        // 检查当前节点是否匹配
        if (node.getName().toLowerCase().contains(keyword) ||
            (node.getDescription() != null && node.getDescription().toLowerCase().contains(keyword)) ||
            (node.getKeywords() != null && node.getKeywords().stream()
                    .anyMatch(k -> k.toLowerCase().contains(keyword)))) {
            results.add(node);
        }

        // 递归搜索子节点
        if (node.getChildren() != null) {
            for (KnowledgeTreeDataResponse.TreeNode child : node.getChildren()) {
                results.addAll(searchNodesRecursively(child, keyword));
            }
        }

        return results;
    }

    /**
     * 根据节点ID获取子树
     * @param treeId 知识树ID
     * @param nodeId 节点ID
     * @param includeNoteInfo 是否包含笔记信息
     * @param maxDepth 最大深度
     * @return 子树数据
     */
    @GetMapping("/{treeId}/subtree/{nodeId}")
    public Object getSubtreeByNodeId(
            @PathVariable Long treeId,
            @PathVariable String nodeId,
            @RequestParam(required = false, defaultValue = "false") Boolean includeNoteInfo,
            @RequestParam(required = false) Integer maxDepth) {
        log.info("获取子树数据，树ID：{}，节点ID：{}，包含笔记信息：{}，最大深度：{}", treeId, nodeId, includeNoteInfo, maxDepth);

        String userId = requestContextUtil.getCurrentUserId();
        if (userId == null) {
            return Statusful.error(new UserLoginError());
        }

        try {
            KnowledgeTreeDataResponse response = knowledgeTreeService.getSubtreeByNodeId(
                userId, treeId, nodeId, includeNoteInfo, maxDepth);
            return Statusful.ok(response);
        } catch (Exception e) {
            log.error("获取子树数据失败", e);
            return Statusful.error(new UserInputParamError("获取子树数据失败：" + e.getMessage()));
        }
    }

    /**
     * 创建知识树请求DTO
     */
    public static class CreateKnowledgeTreeRequest {
        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * 知识树统计信息DTO
     */
    public static class KnowledgeTreeStatistics {
        private Long treeId;
        private String name;
        private Integer totalNodes;
        private Integer associatedNotesCount;
        private String lastUpdateTime;

        public Long getTreeId() {
            return treeId;
        }

        public void setTreeId(Long treeId) {
            this.treeId = treeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getTotalNodes() {
            return totalNodes;
        }

        public void setTotalNodes(Integer totalNodes) {
            this.totalNodes = totalNodes;
        }

        public Integer getAssociatedNotesCount() {
            return associatedNotesCount;
        }

        public void setAssociatedNotesCount(Integer associatedNotesCount) {
            this.associatedNotesCount = associatedNotesCount;
        }

        public String getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(String lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }
    }
}
