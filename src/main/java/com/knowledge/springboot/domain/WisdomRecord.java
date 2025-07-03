package com.knowledge.springboot.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

/**
 * 智慧值记录实体类
 */
@Document(collection = "wisdom_records")
public class WisdomRecord {
    
    @Id
    private String id;
    
    private String userId;
    
    private String noteId;
    
    private String knowledgePointId; // 可选
    
    private Double baseWisdomValue;
    
    private Double currentWisdomValue;
    
    private String difficultyLevel; // beginner, intermediate, advanced, expert
    
    private Double difficultyCoefficient;
    
    private String importanceLevel; // auxiliary, general, important, core
    
    private Double importanceCoefficient;
    
    private String qualityLevel; // excellent, good, pass, fail
    
    private Double qualityCoefficient;
    
    private String learningMethod; // reading, practice, hands-on, creation
    
    private Double learningMethodCoefficient;
    
    private String knowledgeType; // theoretical, practical, memory, understanding
    
    private Double knowledgeTypeCoefficient;
    
    private Map<String, String> calculationBreakdown;
    
    private String evaluationSummary;
    
    private Date createdAt;
    
    private Date updatedAt;
    
    // 智慧值衰减相关字段
    private Double memoryStability = 24.0; // 记忆稳定性参数，默认24小时
    
    private Date lastStudyTime; // 最后学习时间
    
    private Date lastReviewTime; // 最后复习时间
    
    private Integer reviewCount = 0; // 复习次数
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public String getKnowledgePointId() {
        return knowledgePointId;
    }
    
    public void setKnowledgePointId(String knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }
    
    public Double getBaseWisdomValue() {
        return baseWisdomValue;
    }
    
    public void setBaseWisdomValue(Double baseWisdomValue) {
        this.baseWisdomValue = baseWisdomValue;
    }
    
    public Double getCurrentWisdomValue() {
        return currentWisdomValue;
    }
    
    public void setCurrentWisdomValue(Double currentWisdomValue) {
        this.currentWisdomValue = currentWisdomValue;
    }
    
    public String getDifficultyLevel() {
        return difficultyLevel;
    }
    
    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
    
    public Double getDifficultyCoefficient() {
        return difficultyCoefficient;
    }
    
    public void setDifficultyCoefficient(Double difficultyCoefficient) {
        this.difficultyCoefficient = difficultyCoefficient;
    }
    
    public String getImportanceLevel() {
        return importanceLevel;
    }
    
    public void setImportanceLevel(String importanceLevel) {
        this.importanceLevel = importanceLevel;
    }
    
    public Double getImportanceCoefficient() {
        return importanceCoefficient;
    }
    
    public void setImportanceCoefficient(Double importanceCoefficient) {
        this.importanceCoefficient = importanceCoefficient;
    }
    
    public String getQualityLevel() {
        return qualityLevel;
    }
    
    public void setQualityLevel(String qualityLevel) {
        this.qualityLevel = qualityLevel;
    }
    
    public Double getQualityCoefficient() {
        return qualityCoefficient;
    }
    
    public void setQualityCoefficient(Double qualityCoefficient) {
        this.qualityCoefficient = qualityCoefficient;
    }
    
    public String getLearningMethod() {
        return learningMethod;
    }
    
    public void setLearningMethod(String learningMethod) {
        this.learningMethod = learningMethod;
    }
    
    public Double getLearningMethodCoefficient() {
        return learningMethodCoefficient;
    }
    
    public void setLearningMethodCoefficient(Double learningMethodCoefficient) {
        this.learningMethodCoefficient = learningMethodCoefficient;
    }
    
    public String getKnowledgeType() {
        return knowledgeType;
    }
    
    public void setKnowledgeType(String knowledgeType) {
        this.knowledgeType = knowledgeType;
    }
    
    public Double getKnowledgeTypeCoefficient() {
        return knowledgeTypeCoefficient;
    }
    
    public void setKnowledgeTypeCoefficient(Double knowledgeTypeCoefficient) {
        this.knowledgeTypeCoefficient = knowledgeTypeCoefficient;
    }
    
    public Map<String, String> getCalculationBreakdown() {
        return calculationBreakdown;
    }
    
    public void setCalculationBreakdown(Map<String, String> calculationBreakdown) {
        this.calculationBreakdown = calculationBreakdown;
    }
    
    public String getEvaluationSummary() {
        return evaluationSummary;
    }
    
    public void setEvaluationSummary(String evaluationSummary) {
        this.evaluationSummary = evaluationSummary;
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
    
    // 智慧值衰减相关字段的getter和setter方法
    public Double getMemoryStability() {
        return memoryStability;
    }
    
    public void setMemoryStability(Double memoryStability) {
        this.memoryStability = memoryStability;
    }
    
    public Date getLastStudyTime() {
        return lastStudyTime;
    }
    
    public void setLastStudyTime(Date lastStudyTime) {
        this.lastStudyTime = lastStudyTime;
    }
    
    public Date getLastReviewTime() {
        return lastReviewTime;
    }
    
    public void setLastReviewTime(Date lastReviewTime) {
        this.lastReviewTime = lastReviewTime;
    }
    
    public Integer getReviewCount() {
        return reviewCount;
    }
    
    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
}
