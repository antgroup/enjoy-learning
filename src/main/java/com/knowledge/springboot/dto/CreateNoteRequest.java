package com.knowledge.springboot.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 创建笔记请求DTO
 */
public class CreateNoteRequest {
    
    @Size(max = 200, message = "笔记标题不能超过200个字符")
    private String title;
    
    @NotBlank(message = "笔记内容不能为空")
    @Size(max = 50000, message = "笔记内容不能超过50000个字符")
    private String content;
    
    private List<String> tags;
    
    private List<String> relatedKnowledgePoints;
    
    @Pattern(regexp = "^(reading|practice|hands-on|creation)$", message = "学习方式必须是reading、practice、hands-on或creation")
    private String learningMethod;
    
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
} 