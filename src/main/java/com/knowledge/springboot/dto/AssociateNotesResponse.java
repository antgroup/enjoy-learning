package com.knowledge.springboot.dto;

import java.util.List;

/**
 * 知识树关联笔记响应DTO
 */
public class AssociateNotesResponse {
    
    private Long treeId;
    private List<ProcessedNote> processedNotes;
    private List<AssociationResult> associationResults;
    private TreeUpdateSummary treeUpdateSummary;
    private String processTime;

    // 内部类：处理的笔记信息
    public static class ProcessedNote {
        private String noteId;
        private String title;
        private String status; // success, failed, partial
        private Integer associatedKnowledgePoints;
        private Integer newNodesCreated;
        private Integer existingNodesUpdated;

        // Getters and Setters
        public String getNoteId() {
            return noteId;
        }

        public void setNoteId(String noteId) {
            this.noteId = noteId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getAssociatedKnowledgePoints() {
            return associatedKnowledgePoints;
        }

        public void setAssociatedKnowledgePoints(Integer associatedKnowledgePoints) {
            this.associatedKnowledgePoints = associatedKnowledgePoints;
        }

        public Integer getNewNodesCreated() {
            return newNodesCreated;
        }

        public void setNewNodesCreated(Integer newNodesCreated) {
            this.newNodesCreated = newNodesCreated;
        }

        public Integer getExistingNodesUpdated() {
            return existingNodesUpdated;
        }

        public void setExistingNodesUpdated(Integer existingNodesUpdated) {
            this.existingNodesUpdated = existingNodesUpdated;
        }
    }

    // 内部类：关联结果
    public static class AssociationResult {
        private String knowledgePointId;
        private String knowledgePointName;
        private String action; // associated, created_new_node, failed
        private String targetNodeId;
        private String targetNodeName;
        private String newNodeId;
        private String parentNodeId;
        private Double relevanceScore;

        // Getters and Setters
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

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getTargetNodeId() {
            return targetNodeId;
        }

        public void setTargetNodeId(String targetNodeId) {
            this.targetNodeId = targetNodeId;
        }

        public String getTargetNodeName() {
            return targetNodeName;
        }

        public void setTargetNodeName(String targetNodeName) {
            this.targetNodeName = targetNodeName;
        }

        public String getNewNodeId() {
            return newNodeId;
        }

        public void setNewNodeId(String newNodeId) {
            this.newNodeId = newNodeId;
        }

        public String getParentNodeId() {
            return parentNodeId;
        }

        public void setParentNodeId(String parentNodeId) {
            this.parentNodeId = parentNodeId;
        }

        public Double getRelevanceScore() {
            return relevanceScore;
        }

        public void setRelevanceScore(Double relevanceScore) {
            this.relevanceScore = relevanceScore;
        }
    }

    // 内部类：知识树更新摘要
    public static class TreeUpdateSummary {
        private Integer totalNodes;
        private Integer newNodesCount;
        private Integer updatedNodesCount;
        private Integer totalAssociations;

        // Getters and Setters
        public Integer getTotalNodes() {
            return totalNodes;
        }

        public void setTotalNodes(Integer totalNodes) {
            this.totalNodes = totalNodes;
        }

        public Integer getNewNodesCount() {
            return newNodesCount;
        }

        public void setNewNodesCount(Integer newNodesCount) {
            this.newNodesCount = newNodesCount;
        }

        public Integer getUpdatedNodesCount() {
            return updatedNodesCount;
        }

        public void setUpdatedNodesCount(Integer updatedNodesCount) {
            this.updatedNodesCount = updatedNodesCount;
        }

        public Integer getTotalAssociations() {
            return totalAssociations;
        }

        public void setTotalAssociations(Integer totalAssociations) {
            this.totalAssociations = totalAssociations;
        }
    }

    // Getters and Setters
    public Long getTreeId() {
        return treeId;
    }

    public void setTreeId(Long treeId) {
        this.treeId = treeId;
    }

    public List<ProcessedNote> getProcessedNotes() {
        return processedNotes;
    }

    public void setProcessedNotes(List<ProcessedNote> processedNotes) {
        this.processedNotes = processedNotes;
    }

    public List<AssociationResult> getAssociationResults() {
        return associationResults;
    }

    public void setAssociationResults(List<AssociationResult> associationResults) {
        this.associationResults = associationResults;
    }

    public TreeUpdateSummary getTreeUpdateSummary() {
        return treeUpdateSummary;
    }

    public void setTreeUpdateSummary(TreeUpdateSummary treeUpdateSummary) {
        this.treeUpdateSummary = treeUpdateSummary;
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }
}
