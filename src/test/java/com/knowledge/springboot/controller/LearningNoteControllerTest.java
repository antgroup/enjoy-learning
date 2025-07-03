package com.knowledge.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.springboot.domain.LearningNote;
import com.knowledge.springboot.dto.CreateNoteRequest;
import com.knowledge.springboot.service.impl.LearningNoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class LearningNoteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LearningNoteService learningNoteService;

    @InjectMocks
    private LearningNoteController learningNoteController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String TEST_NOTE_ID = "test_note_id";
    private final String TEST_USER_ID = "test_user_id";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(learningNoteController).build();
    }

    @Test
    public void testUploadNote() throws Exception {
        // 准备测试数据
        CreateNoteRequest request = new CreateNoteRequest();
        request.setTitle("测试笔记标题");
        request.setContent("测试笔记内容");

        LearningNote note = new LearningNote();
        note.setId(TEST_NOTE_ID);
        note.setTitle("测试笔记标题");
        note.setContent("测试笔记内容");
        note.setUserId(TEST_USER_ID);

        LearningNote.AIAnalysis aiAnalysis = new LearningNote.AIAnalysis();
        aiAnalysis.setStatus("pending");
        note.setAiAnalysis(aiAnalysis);

        // 模拟服务方法
        when(learningNoteService.createNote(any(CreateNoteRequest.class), eq(TEST_USER_ID))).thenReturn(note);

        // 执行测试
        mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.noteId").value(TEST_NOTE_ID))
                .andExpect(jsonPath("$.data.title").value("测试笔记标题"))
                .andExpect(jsonPath("$.data.analysisStatus").value("pending"));
    }

    @Test
    public void testGetNoteAnalysisStatusCompleted() throws Exception {
        // 准备测试数据
        Map<String, Object> analysisStatus = new HashMap<>();
        analysisStatus.put("analysisStatus", "completed");

        Map<String, Object> aiAnalysis = new HashMap<>();
        aiAnalysis.put("extractedKeywords", Arrays.asList("关键词1", "关键词2"));
        aiAnalysis.put("summary", "测试摘要");
        aiAnalysis.put("subjectArea", "前端开发");
        analysisStatus.put("aiAnalysis", aiAnalysis);

        Map<String, Object> wisdomValue = new HashMap<>();
        wisdomValue.put("baseWisdomValue", 100.0);
        wisdomValue.put("difficultyLevel", "intermediate");
        wisdomValue.put("importanceLevel", "core");
        wisdomValue.put("evaluationSummary", "测试评估摘要");
        analysisStatus.put("wisdomValue", wisdomValue);

        // 模拟服务方法
        when(learningNoteService.getNoteAnalysisStatus(TEST_NOTE_ID)).thenReturn(analysisStatus);

        // 执行测试
        mockMvc.perform(get("/api/notes/{noteId}/analysis", TEST_NOTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.analysisStatus").value("completed"))
                .andExpect(jsonPath("$.data.aiAnalysis").exists())
                .andExpect(jsonPath("$.data.wisdomValue").exists());
    }

    @Test
    public void testGetNoteAnalysisStatusPending() throws Exception {
        // 准备测试数据
        Map<String, Object> analysisStatus = new HashMap<>();
        analysisStatus.put("analysisStatus", "pending");

        // 模拟服务方法
        when(learningNoteService.getNoteAnalysisStatus(TEST_NOTE_ID)).thenReturn(analysisStatus);

        // 执行测试
        mockMvc.perform(get("/api/notes/{noteId}/analysis", TEST_NOTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.analysisStatus").value("pending"));
    }

    @Test
    public void testGetNoteAnalysisStatusFailed() throws Exception {
        // 准备测试数据
        Map<String, Object> analysisStatus = new HashMap<>();
        analysisStatus.put("analysisStatus", "failed");
        analysisStatus.put("errorMessage", "测试错误信息");

        // 模拟服务方法
        when(learningNoteService.getNoteAnalysisStatus(TEST_NOTE_ID)).thenReturn(analysisStatus);

        // 执行测试
        mockMvc.perform(get("/api/notes/{noteId}/analysis", TEST_NOTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.analysisStatus").value("failed"))
                .andExpect(jsonPath("$.data.errorMessage").value("测试错误信息"));
    }

    @Test
    public void testGetNoteAnalysisStatusNotFound() throws Exception {
        // 模拟服务方法
        when(learningNoteService.getNoteAnalysisStatus(TEST_NOTE_ID)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/notes/{noteId}/analysis", TEST_NOTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("A0400"));
    }

    @Test
    public void testGetNoteDetail() throws Exception {
        // 准备测试数据
        LearningNote note = new LearningNote();
        note.setId(TEST_NOTE_ID);
        note.setTitle("测试笔记标题");
        note.setContent("测试笔记内容");
        note.setUserId(TEST_USER_ID);

        // 模拟服务方法
        when(learningNoteService.getNoteById(TEST_NOTE_ID)).thenReturn(note);

        // 执行测试
        mockMvc.perform(get("/api/notes/{noteId}", TEST_NOTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.id").value(TEST_NOTE_ID))
                .andExpect(jsonPath("$.data.title").value("测试笔记标题"));
    }

    @Test
    public void testGetNoteDetailNotFound() throws Exception {
        // 模拟服务方法
        when(learningNoteService.getNoteById(TEST_NOTE_ID)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/notes/{noteId}", TEST_NOTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("A0400"));
    }

    @Test
    public void testGetUserNotes() throws Exception {
        // 准备测试数据
        List<LearningNote> notes = new ArrayList<>();

        LearningNote note1 = new LearningNote();
        note1.setId("note1");
        note1.setTitle("笔记1");
        note1.setUserId(TEST_USER_ID);
        notes.add(note1);

        LearningNote note2 = new LearningNote();
        note2.setId("note2");
        note2.setTitle("笔记2");
        note2.setUserId(TEST_USER_ID);
        notes.add(note2);

        // 模拟服务方法
        when(learningNoteService.getNotesByUserId(TEST_USER_ID)).thenReturn(notes);

        // 执行测试
        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    public void testDeleteNote() throws Exception {
        // 模拟服务方法
        when(learningNoteService.deleteNote(TEST_NOTE_ID, TEST_USER_ID)).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/api/notes/{noteId}", TEST_NOTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    public void testDeleteNoteNotFound() throws Exception {
        // 模拟服务方法
        when(learningNoteService.deleteNote(TEST_NOTE_ID, TEST_USER_ID)).thenReturn(false);

        // 执行测试
        mockMvc.perform(delete("/api/notes/{noteId}", TEST_NOTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("A0400"));
    }
} 