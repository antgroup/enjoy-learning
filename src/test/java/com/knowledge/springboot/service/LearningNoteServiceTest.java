package com.knowledge.springboot.service;

import com.alibaba.fastjson2.JSON;
import com.knowledge.springboot.domain.LearningNote;
import com.knowledge.springboot.dto.CreateNoteRequest;
import com.knowledge.springboot.repository.LearningNoteRepository;
import com.knowledge.springboot.repository.WisdomRecordRepository;
import com.knowledge.springboot.service.impl.AIAnalysisService;
import com.knowledge.springboot.service.impl.LearningNoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class LearningNoteServiceTest {

    @Autowired
    private LearningNoteService learningNoteService;

    @Autowired
    private LearningNoteRepository learningNoteRepository;

    @Autowired
    private WisdomRecordRepository wisdomRecordRepository;

    @Autowired
    private AIAnalysisService aiAnalysisService;

    private final String TEST_USER_ID = "test_user_id";
    private final String TEST_NOTE_ID = "test_note_id";

    @Test
    @Rollback(false) // 不回滚事务，保留测试数据
    public void testCreateNote() {
        // 准备测试数据
        LearningNote note = new LearningNote();
        note.setTitle("MongoDB集成测试笔记");
        note.setContent("这是一个测试MongoDB集成的笔记内容");
        note.setUserId(TEST_USER_ID);
        note.setStatus("active");

        List<String> tags = new ArrayList<>();
        tags.add("测试");
        tags.add("MongoDB");
        note.setTags(tags);

        note.setLearningMethod("reading");

        // 设置AI分析状态
        LearningNote.AIAnalysis aiAnalysis = new LearningNote.AIAnalysis();
        aiAnalysis.setStatus("pending");
        note.setAiAnalysis(aiAnalysis);

        // 设置创建和更新时间
        Date now = new Date();
        note.setCreatedAt(now);
        note.setUpdatedAt(now);

        // 直接使用真实的仓库保存数据
        LearningNote savedNote = learningNoteRepository.save(note);
        System.out.println("保存的笔记ID: " + savedNote.getId());
        System.out.println("保存的笔记数据: " + JSON.toJSONString(savedNote));

        // 验证结果
        assertNotNull(savedNote);
        assertNotNull(savedNote.getId());
        assertEquals("MongoDB集成测试笔记", savedNote.getTitle());
        assertEquals("这是一个测试MongoDB集成的笔记内容", savedNote.getContent());
        assertEquals(TEST_USER_ID, savedNote.getUserId());
        assertEquals("active", savedNote.getStatus());
        assertEquals("pending", savedNote.getAiAnalysis().getStatus());
        assertEquals(2, savedNote.getTags().size());
        assertTrue(savedNote.getTags().contains("MongoDB"));
    }

    @Test
    @Rollback(false)
    public void testCreateNoteWithoutTitle() {
        // 准备测试数据
        CreateNoteRequest request = new CreateNoteRequest();
        request.setContent("测试笔记内容 - 无标题2");
        // 不设置标题

        List<String> tags = new ArrayList<>();
        tags.add("测试");
        tags.add("无标题");
        request.setTags(tags);

        request.setLearningMethod("reading");

        // 调用服务方法
        LearningNote result = learningNoteService.createNote(request, TEST_USER_ID);
        System.out.println("创建的无标题笔记ID: " + result.getId());
        System.out.println("创建的无标题笔记数据: " + JSON.toJSONString(result));

        // 验证结果
//        assertNotNull(result);
//        assertNotNull(result.getId());
//        assertEquals("笔记分析中...", result.getTitle());
//        assertEquals("测试笔记内容 - 无标题", result.getContent());
//        assertEquals(TEST_USER_ID, result.getUserId());
//        assertEquals("active", result.getStatus());
//        assertEquals("pending", result.getAiAnalysis().getStatus());
    }

    @Test
    public void testGetNoteById() {
        // 先创建一个测试笔记
        LearningNote note = new LearningNote();
        note.setTitle("测试笔记标题");
        note.setContent("测试笔记内容");
        note.setUserId(TEST_USER_ID);
        note.setStatus("active");

        // 设置AI分析状态
        LearningNote.AIAnalysis aiAnalysis = new LearningNote.AIAnalysis();
        aiAnalysis.setStatus("pending");
        note.setAiAnalysis(aiAnalysis);

        // 设置创建和更新时间
        Date now = new Date();
        note.setCreatedAt(now);
        note.setUpdatedAt(now);

        // 保存笔记
        LearningNote savedNote = learningNoteRepository.save(note);
        String noteId = savedNote.getId();

        // 调用服务方法
        LearningNote result = learningNoteService.getNoteById(noteId);

        // 验证结果
        assertNotNull(result);
        assertEquals(noteId, result.getId());
        assertEquals("测试笔记标题", result.getTitle());
        assertEquals("测试笔记内容", result.getContent());
    }

    @Test
    public void testGetNoteByIdNotFound() {
        // 使用一个不存在的ID
        String nonExistentId = "non_existent_id";

        // 调用服务方法
        LearningNote result = learningNoteService.getNoteById(nonExistentId);

        // 验证结果
        assertNull(result);
    }

    @Test
    @Rollback(false)
    public void testGetNotesByUserId() {
        // 先创建几个测试笔记
        for (int i = 1; i <= 3; i++) {
            LearningNote note = new LearningNote();
            note.setTitle("用户笔记 " + i);
            note.setContent("用户笔记内容 " + i);
            note.setUserId(TEST_USER_ID);
            note.setStatus("active");

            // 设置AI分析状态
            LearningNote.AIAnalysis aiAnalysis = new LearningNote.AIAnalysis();
            aiAnalysis.setStatus("pending");
            note.setAiAnalysis(aiAnalysis);

            // 设置创建和更新时间
            Date now = new Date();
            note.setCreatedAt(now);
            note.setUpdatedAt(now);

            learningNoteRepository.save(note);
        }

        // 调用服务方法
        List<LearningNote> result = learningNoteService.getNotesByUserId(TEST_USER_ID);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() >= 3); // 可能有之前测试创建的笔记

        // 打印结果
        System.out.println("查询到的用户笔记数量: " + result.size());
        for (LearningNote note : result) {
            System.out.println("笔记ID: " + note.getId() + ", 标题: " + note.getTitle());
        }
    }

    @Test
    @Rollback(false)
    public void testDeleteNote() {
        // 先创建一个测试笔记
        LearningNote note = new LearningNote();
        note.setTitle("待删除笔记");
        note.setContent("待删除笔记内容");
        note.setUserId(TEST_USER_ID);
        note.setStatus("active");

        // 设置AI分析状态
        LearningNote.AIAnalysis aiAnalysis = new LearningNote.AIAnalysis();
        aiAnalysis.setStatus("pending");
        note.setAiAnalysis(aiAnalysis);

        // 设置创建和更新时间
        Date now = new Date();
        note.setCreatedAt(now);
        note.setUpdatedAt(now);

        // 保存笔记
        LearningNote savedNote = learningNoteRepository.save(note);
        String noteId = savedNote.getId();

        System.out.println("创建待删除笔记，ID: " + noteId);

        // 调用服务方法删除笔记
        boolean result = learningNoteService.deleteNote(noteId, TEST_USER_ID);

        // 验证结果
        assertTrue(result);

        // 查询笔记验证状态
        LearningNote deletedNote = learningNoteRepository.findById(noteId).orElse(null);
        assertNotNull(deletedNote);
        assertEquals("deleted", deletedNote.getStatus());

        System.out.println("笔记删除后状态: " + deletedNote.getStatus());
    }
}
