package com.knowledge.springboot.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;

/**
 * 知识树实体类
 */
@Document(collection = "knowledge_trees")
public class KnowledgeTree {

    @Id
    private String id;

    @Field(value = "treeId", targetType = FieldType.INT64)
    private Long treeId;

    @Field("userId")
    private String userId;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("category")
    private String category;

    @Field("rootNodeId")
    private String rootNodeId;

    @Field(value = "totalNodes", targetType = FieldType.INT64)
    private Long totalNodes;

    @Field(value = "maxDepth", targetType = FieldType.INT64)
    private Long maxDepth;

    @Field(value = "associatedNotesCount", targetType = FieldType.INT64)
    private Long associatedNotesCount;

    @Field("lastUpdateTime")
    private Date lastUpdateTime;

    @Field("createdAt")
    private Date createdAt;

    @Field("updatedAt")
    private Date updatedAt;

    @Field("status")
    private String status; // 知识树状态：idle(空闲)、processing(处理中)、success(成功)、failed(失败)

    @Transient
    private String treeIdStr;

    // 构造函数
    public KnowledgeTree() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.totalNodes = 0L;
        this.maxDepth = 0L;
        this.associatedNotesCount = 0L;
        this.status = "idle"; // 默认状态为空闲
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTreeId() {
        return treeId;
    }

    public void setTreeId(Long treeId) {
        this.treeId = treeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRootNodeId() {
        return rootNodeId;
    }

    public void setRootNodeId(String rootNodeId) {
        this.rootNodeId = rootNodeId;
    }

    public Long getTotalNodes() {
        return totalNodes;
    }

    public void setTotalNodes(Long totalNodes) {
        this.totalNodes = totalNodes;
    }

    public Long getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(Long maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Long getAssociatedNotesCount() {
        return associatedNotesCount;
    }

    public void setAssociatedNotesCount(Long associatedNotesCount) {
        this.associatedNotesCount = associatedNotesCount;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTreeIdStr() {
        return treeIdStr;
    }

    public void setTreeIdStr(String treeIdStr) {
        this.treeIdStr = treeIdStr;
    }
}
