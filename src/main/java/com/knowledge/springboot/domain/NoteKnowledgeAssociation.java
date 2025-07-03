package com.knowledge.springboot.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * 笔记知识点关联实体类
 */
@Document(collection = "note_knowledge_associations")
public class NoteKnowledgeAssociation {
    
    @Id
    private String id;
    
    @Field("associationId")
    private String associationId;
    
    @Field("userId")
    private String userId;
    
    @Field("noteId")
    private String noteId;
    
    @Field("treeId")
    private Long treeId;
    
    @Field("nodeId")
    private String nodeId;
    
    @Field("knowledgePointId")
    private String knowledgePointId;
    
    @Field("knowledgePointName")
    private String knowledgePointName;
    
    @Field("matchType")
    private String matchType; // exact, semantic, new
    
    @Field("relevanceScore")
    private Double relevanceScore;
    
    @Field("matchingKeywords")
    private List<String> matchingKeywords;
    
    @Field("associationReason")
    private String associationReason;
    
    @Field("isManualConfirmed")
    private Boolean isManualConfirmed;
    
    @Field("createdAt")
    private Date createdAt;
    
    @Field("updatedAt")
    private Date updatedAt;

    // 构造函数
    public NoteKnowledgeAssociation() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isManualConfirmed = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssociationId() {
        return associationId;
    }

    public void setAssociationId(String associationId) {
        this.associationId = associationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public Long getTreeId() {
        return treeId;
    }

    public void setTreeId(Long treeId) {
        this.treeId = treeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(String knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getKnowledgePointName() {
        return knowledgePointName;
    }

    public void setKnowledgePointName(String knowledgePointName) {
        this.knowledgePointName = knowledgePointName;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public Double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public List<String> getMatchingKeywords() {
        return matchingKeywords;
    }

    public void setMatchingKeywords(List<String> matchingKeywords) {
        this.matchingKeywords = matchingKeywords;
    }

    public String getAssociationReason() {
        return associationReason;
    }

    public void setAssociationReason(String associationReason) {
        this.associationReason = associationReason;
    }

    public Boolean getIsManualConfirmed() {
        return isManualConfirmed;
    }

    public void setIsManualConfirmed(Boolean isManualConfirmed) {
        this.isManualConfirmed = isManualConfirmed;
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
}
