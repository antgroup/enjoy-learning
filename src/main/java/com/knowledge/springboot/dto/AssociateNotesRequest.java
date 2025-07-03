package com.knowledge.springboot.dto;

import java.util.List;

/**
 * 知识树关联笔记请求DTO
 */
public class AssociateNotesRequest {
    
    private String userId;
    private Long treeId;
    private List<String> noteIds;
    private String associationMode; // auto, manual
    private Boolean overrideExisting;

    public AssociateNotesRequest() {
        this.associationMode = "auto";
        this.overrideExisting = false;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTreeId() {
        return treeId;
    }

    public void setTreeId(Long treeId) {
        this.treeId = treeId;
    }

    public List<String> getNoteIds() {
        return noteIds;
    }

    public void setNoteIds(List<String> noteIds) {
        this.noteIds = noteIds;
    }

    public String getAssociationMode() {
        return associationMode;
    }

    public void setAssociationMode(String associationMode) {
        this.associationMode = associationMode;
    }

    public Boolean getOverrideExisting() {
        return overrideExisting;
    }

    public void setOverrideExisting(Boolean overrideExisting) {
        this.overrideExisting = overrideExisting;
    }
}
