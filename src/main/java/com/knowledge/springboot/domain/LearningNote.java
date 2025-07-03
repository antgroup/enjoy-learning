package com.knowledge.springboot.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 学习笔记实体类
 */
@Document(collection = "learning_notes")
public class LearningNote {

    @Id
    private String id;

    private String userId;

    private String title;

    private String content;

    private String rawContent; // Markdown格式的笔记内容，由AI模型生成

    private List<String> tags;

    private List<String> relatedKnowledgePoints;

    private String learningMethod; // reading, practice, hands-on, creation

    private AIAnalysis aiAnalysis;

    private String status; // active, deleted

    private Date createdAt;

    private Date updatedAt;

    @Transient
    private String currentWisdom;

    /**
     * AI分析结果内部类
     */
    public static class AIAnalysis {
        private String status; // pending, completed, failed
        private List<String> extractedKeywords;
        private List<KnowledgePoint> knowledgePoints;
        private String summary;
        private String subjectArea; // AI判断的学科领域
        private Date analyzedAt;
        private String errorMessage;

        public static class KnowledgePoint {
            private String concept;
            private Double importance;
            private String category;

            public String getConcept() {
                return concept;
            }

            public void setConcept(String concept) {
                this.concept = concept;
            }

            public Double getImportance() {
                return importance;
            }

            public void setImportance(Double importance) {
                this.importance = importance;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<String> getExtractedKeywords() {
            return extractedKeywords;
        }

        public void setExtractedKeywords(List<String> extractedKeywords) {
            this.extractedKeywords = extractedKeywords;
        }

        public List<KnowledgePoint> getKnowledgePoints() {
            return knowledgePoints;
        }

        public void setKnowledgePoints(List<KnowledgePoint> knowledgePoints) {
            this.knowledgePoints = knowledgePoints;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getSubjectArea() {
            return subjectArea;
        }

        public void setSubjectArea(String subjectArea) {
            this.subjectArea = subjectArea;
        }

        public Date getAnalyzedAt() {
            return analyzedAt;
        }

        public void setAnalyzedAt(Date analyzedAt) {
            this.analyzedAt = analyzedAt;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getRelatedKnowledgePoints() {
        return relatedKnowledgePoints;
    }

    public void setRelatedKnowledgePoints(List<String> relatedKnowledgePoints) {
        this.relatedKnowledgePoints = relatedKnowledgePoints;
    }

    public String getLearningMethod() {
        return learningMethod;
    }

    public void setLearningMethod(String learningMethod) {
        this.learningMethod = learningMethod;
    }

    public AIAnalysis getAiAnalysis() {
        return aiAnalysis;
    }

    public void setAiAnalysis(AIAnalysis aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCurrentWisdom() {
        return currentWisdom;
    }

    public void setCurrentWisdom(String currentWisdom) {
        this.currentWisdom = currentWisdom;
    }
}
