import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { notesApi, CreateNoteRequest, NoteListItem } from '../../api';
import './index.css';

const Notes: React.FC = () => {
  const navigate = useNavigate();
  const [notes, setNotes] = useState<NoteListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreateMenu, setShowCreateMenu] = useState(false);
  const [showTextEditor, setShowTextEditor] = useState(false);
  const [editingNote, setEditingNote] = useState<NoteListItem | null>(null);
  const [textTitle, setTextTitle] = useState('');
  const [textContent, setTextContent] = useState('');
  const [uploading, setUploading] = useState(false);
  const [apiResponse, setApiResponse] = useState<any>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // 页面加载时获取笔记列表
  useEffect(() => {
    fetchNotes();
  }, []);

  const fetchNotes = async () => {
    try {
      setLoading(true);
      const response = await notesApi.getNoteList('', 1, 50); // 获取前50条笔记
      
      // 保存完整的API响应用于调试显示
      setApiResponse(response);
      
      if (response.code === '00000' && response.data) {
        setNotes(response.data.list || []);
      } else {
        console.error('获取笔记列表失败:', response.errorMessage);
        setNotes([]);
      }
    } catch (error: any) {
      console.error('获取笔记列表失败:', error);
      setNotes([]);
      setApiResponse({ error: error.message });
    } finally {
      setLoading(false);
    }
  };

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const getFileIcon = (type: string): string => {
    if (type.includes('pdf')) return '📄';
    if (type.startsWith('image/')) return '🖼️';
    if (type.startsWith('video/')) return '🎥';
    if (type.startsWith('audio/')) return '🎵';
    if (type.includes('word')) return '📝';
    if (type.includes('excel') || type.includes('spreadsheet')) return '📊';
    if (type.includes('powerpoint') || type.includes('presentation')) return '📽️';
    if (type.includes('text')) return '📃';
    return '📁';
  };

  const handleCreateText = () => {
    setShowCreateMenu(false);
    setShowTextEditor(true);
    setEditingNote(null);
    setTextTitle('');
    setTextContent('');
  };

  const handleUploadFile = () => {
    setShowCreateMenu(false);
    fileInputRef.current?.click();
  };

  const handleFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;
    if (!files || files.length === 0) return;

    const file = files[0];
    
    // 检查文件类型，只允许txt文件
    if (!file.name.toLowerCase().endsWith('.txt')) {
      alert('只支持上传txt格式的文件');
      return;
    }

    // 检查文件大小，限制5MB
    if (file.size > 5 * 1024 * 1024) {
      alert('文件大小不能超过5MB');
      return;
    }

    try {
      setUploading(true);
      
      const uploadData = {
        title: file.name.replace('.txt', ''), // 去掉扩展名作为标题
        file: file,
        tags: ['文件笔记'],
        subject: '学习资料'
      };

      const response = await notesApi.uploadNote(uploadData);
      
      if (response.code === '00000' && response.data) {
        console.log('文件上传成功:', response.data);
        alert(`文件上传成功！预计${response.data.estimatedAnalysisTime}完成AI分析`);
        
        // 重新获取笔记列表
        await fetchNotes();
      } else {
        throw new Error(response.errorMessage || '文件上传失败');
      }
    } catch (error: any) {
      console.error('文件上传失败:', error);
      alert(error.message || '文件上传失败，请重试');
    } finally {
      setUploading(false);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  const handleSaveText = async () => {
    if (!textTitle.trim() || !textContent.trim()) {
      alert('请输入标题和内容');
      return;
    }

    try {
      setUploading(true);

      if (editingNote) {
        // 编辑现有笔记
        const updateData = {
          title: textTitle,
          content: textContent,
        };
        
        const response = await notesApi.updateNote(editingNote.noteId, updateData);
        
        if (response.code === '00000') {
          console.log('笔记更新成功');
          alert('笔记更新成功！');
        } else {
          throw new Error(response.errorMessage || '笔记更新失败');
        }
      } else {
        // 创建新笔记
        const createData: CreateNoteRequest = {
          title: textTitle,
          content: textContent,
          tags: ['文字笔记'],
          learningMethod: 'reading'
        };
        
        const response = await notesApi.createNote(createData);
        
        if (response.code === '00000' && response.data) {
          console.log('笔记创建成功:', response.data);
          alert(`笔记创建成功！预计${response.data.estimatedTime}秒完成AI分析`);
        } else {
          throw new Error(response.errorMessage || '笔记创建失败');
        }
      }

      // 重新获取笔记列表
      await fetchNotes();
      
      setShowTextEditor(false);
      setEditingNote(null);
      setTextTitle('');
      setTextContent('');
    } catch (error: any) {
      console.error('保存笔记失败:', error);
      alert(error.message || '保存笔记失败，请重试');
    } finally {
      setUploading(false);
    }
  };

  // 点击笔记项跳转到详情页
  const handleNoteClick = (note: NoteListItem) => {
    navigate(`/note/${note.noteId}`);
  };

  const handleEditNote = (note: NoteListItem) => {
    // 只有文字笔记才能编辑
    if (note.title && !note.title.endsWith('.txt')) {
      setEditingNote(note);
      setTextTitle(note.title);
      setTextContent(''); // 需要从详情接口获取内容，这里暂时为空
      setShowTextEditor(true);
    }
  };

  const handleDeleteNote = async (noteId: string) => {
    if (!confirm('确定要删除这条笔记吗？')) {
      return;
    }

    try {
      const response = await notesApi.deleteNote(noteId);
      
      if (response.code === '00000') {
        console.log('笔记删除成功');
        alert('笔记删除成功！');
        
        // 重新获取笔记列表
        await fetchNotes();
      } else {
        throw new Error(response.errorMessage || '删除失败');
      }
    } catch (error: any) {
      console.error('删除笔记失败:', error);
      alert(error.message || '删除笔记失败，请重试');
    }
  };

  const getStatusText = (status: string): string => {
    switch (status) {
      case 'completed': return '分析完成';
      case 'pending': return '分析中';
      case 'processing': return '处理中';
      case 'failed': return '分析失败';
      default: return status;
    }
  };

  const getStatusColor = (status: string): string => {
    switch (status) {
      case 'completed': return '#52c41a';
      case 'pending': return '#1890ff';
      case 'processing': return '#faad14';
      case 'failed': return '#ff4d4f';
      default: return '#666';
    }
  };

  // 处理智慧值点击事件
  const handleWisdomClick = (e: React.MouseEvent) => {
    e.stopPropagation(); // 阻止事件冒泡，避免触发卡片点击
  };

  if (loading) {
    return (
      <div className="notes-container">
        <div className="loading-state">
          <div className="spinner"></div>
          <div>加载中...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="notes-container">
      {/* 页面标题 */}
      <div className="notes-header">
        <h1 className="notes-title">我的笔记</h1>
        <p className="notes-subtitle">记录您的学习心得和重要资料</p>
      </div>

      {/* 笔记卡片列表 */}
      {apiResponse && apiResponse.data && Array.isArray(apiResponse.data) && (
        <div style={{
          margin: '20px 0'
        }}>
          <h3 style={{ margin: '0 0 20px 0', fontSize: '18px', color: '#333', fontWeight: 'bold' }}>
            笔记列表 (共 {apiResponse.data.length} 条记录)
          </h3>
          
          {apiResponse.data.length > 0 ? (
            <div style={{ 
              display: 'grid', 
              gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', 
              gap: '20px',
              maxWidth: '100%'
            }}>
              {apiResponse.data.map((note: any, index: number) => (
                <div key={note.id || index} style={{
                  position: 'relative',
                  padding: '20px',
                  backgroundColor: '#fff',
                  border: '1px solid #e9ecef',
                  borderRadius: '12px',
                  boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                  transition: 'transform 0.2s, box-shadow 0.2s',
                  cursor: 'pointer',
                  maxWidth: '100%',
                  overflow: 'hidden'
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = 'translateY(-2px)';
                  e.currentTarget.style.boxShadow = '0 4px 8px rgba(0,0,0,0.15)';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = 'translateY(0)';
                  e.currentTarget.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
                }}>
                  
                  {/* 智慧值显示 - 右上角 */}
                  {note.currentWisdom && (
                    <div 
                      title="智慧值"
                      onClick={handleWisdomClick}
                      style={{
                        position: 'absolute',
                        top: '12px',
                        right: '12px',
                        padding: '4px 8px',
                        backgroundColor: '#f0f8ff',
                        border: '1px solid #1890ff',
                        borderRadius: '12px',
                        fontSize: '12px',
                        fontWeight: 'bold',
                        color: '#1890ff',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '4px',
                        cursor: 'pointer',
                        transition: 'all 0.2s ease',
                        zIndex: 10
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.backgroundColor = '#e6f7ff';
                        e.currentTarget.style.transform = 'scale(1.05)';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.backgroundColor = '#f0f8ff';
                        e.currentTarget.style.transform = 'scale(1)';
                      }}
                    >
                      <span>🧠</span>
                      <span>{note.currentWisdom}</span>
                    </div>
                  )}

                  {/* 标题和状态 */}
                  <div style={{ marginBottom: '15px', paddingRight: note.currentWisdom ? '80px' : '0' }}>
                    <h4 style={{ 
                      margin: '0 0 8px 0', 
                      fontSize: '16px', 
                      fontWeight: 'bold',
                      color: '#333',
                      lineHeight: '1.4',
                      wordBreak: 'break-word'
                    }}>
                      {note.title || '无标题'}
                    </h4>
                    <div style={{ display: 'flex', gap: '8px', alignItems: 'center', flexWrap: 'wrap' }}>
                      <span style={{
                        padding: '2px 8px',
                        backgroundColor: note.status === 'active' ? '#d4edda' : '#f8d7da',
                        color: note.status === 'active' ? '#155724' : '#721c24',
                        borderRadius: '12px',
                        fontSize: '12px',
                        fontWeight: '500'
                      }}>
                        {note.status === 'active' ? '活跃' : note.status}
                      </span>
                      {note.aiAnalysis?.status && (
                        <span style={{
                          padding: '2px 8px',
                          backgroundColor: note.aiAnalysis.status === 'completed' ? '#d1ecf1' : '#fff3cd',
                          color: note.aiAnalysis.status === 'completed' ? '#0c5460' : '#856404',
                          borderRadius: '12px',
                          fontSize: '12px',
                          fontWeight: '500'
                        }}>
                          AI分析: {note.aiAnalysis.status === 'completed' ? '已完成' : note.aiAnalysis.status}
                        </span>
                      )}
                    </div>
                  </div>

                  {/* 内容预览 */}
                  {note.content && (
                    <div style={{ marginBottom: '15px' }}>
                      <p style={{
                        margin: '0',
                        fontSize: '14px',
                        color: '#666',
                        lineHeight: '1.5',
                        display: '-webkit-box',
                        WebkitLineClamp: 3,
                        WebkitBoxOrient: 'vertical',
                        overflow: 'hidden',
                        wordBreak: 'break-word'
                      }}>
                        {note.content}
                      </p>
                    </div>
                  )}

                  {/* 标签 */}
                  {note.tags && note.tags.length > 0 && (
                    <div style={{ marginBottom: '15px' }}>
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                        {note.tags.map((tag: string, tagIndex: number) => (
                          <span key={tagIndex} style={{
                            padding: '4px 8px',
                            backgroundColor: '#e9ecef',
                            color: '#495057',
                            borderRadius: '8px',
                            fontSize: '12px'
                          }}>
                            #{tag}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* AI分析摘要 */}
                  {note.aiAnalysis?.summary && (
                    <div style={{ marginBottom: '15px' }}>
                      <h5 style={{ 
                        margin: '0 0 8px 0', 
                        fontSize: '13px', 
                        fontWeight: 'bold',
                        color: '#495057'
                      }}>
                        AI摘要:
                      </h5>
                      <p style={{
                        margin: '0',
                        fontSize: '13px',
                        color: '#6c757d',
                        lineHeight: '1.4',
                        fontStyle: 'italic',
                        wordBreak: 'break-word'
                      }}>
                        {note.aiAnalysis.summary}
                      </p>
                    </div>
                  )}

                  {/* 关键词 */}
                  {note.aiAnalysis?.extractedKeywords && note.aiAnalysis.extractedKeywords.length > 0 && (
                    <div style={{ marginBottom: '15px' }}>
                      <h5 style={{ 
                        margin: '0 0 8px 0', 
                        fontSize: '13px', 
                        fontWeight: 'bold',
                        color: '#495057'
                      }}>
                        关键词:
                      </h5>
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                        {note.aiAnalysis.extractedKeywords.map((keyword: string, keywordIndex: number) => (
                          <span key={keywordIndex} style={{
                            padding: '2px 6px',
                            backgroundColor: '#fff3cd',
                            color: '#856404',
                            borderRadius: '6px',
                            fontSize: '11px'
                          }}>
                            {keyword}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* 时间信息 */}
                  <div style={{ 
                    borderTop: '1px solid #e9ecef', 
                    paddingTop: '12px',
                    fontSize: '12px',
                    color: '#6c757d'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                      <span>创建时间:</span>
                      <span style={{ wordBreak: 'break-word' }}>{note.createdAt}</span>
                    </div>
                    {note.aiAnalysis?.analyzedAt && (
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                        <span>分析时间:</span>
                        <span style={{ wordBreak: 'break-word' }}>{note.aiAnalysis.analyzedAt}</span>
                      </div>
                    )}
                    {note.aiAnalysis?.subjectArea && (
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <span>学科领域:</span>
                        <span style={{ wordBreak: 'break-word' }}>{note.aiAnalysis.subjectArea}</span>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div style={{ 
              padding: '40px', 
              textAlign: 'center', 
              color: '#6c757d',
              backgroundColor: '#fff',
              border: '1px solid #e9ecef',
              borderRadius: '12px'
            }}>
              <div style={{ fontSize: '48px', marginBottom: '16px' }}>📝</div>
              <div style={{ fontSize: '16px', fontWeight: '500' }}>暂无笔记数据</div>
            </div>
          )}
        </div>
      )}
      
      {/* 如果有错误，显示错误信息 */}
      {apiResponse && apiResponse.error && (
        <div style={{
          margin: '20px 0',
          padding: '20px',
          backgroundColor: '#f8d7da',
          border: '1px solid #f5c6cb',
          borderRadius: '12px',
          color: '#721c24'
        }}>
          <h3 style={{ margin: '0 0 12px 0', fontSize: '16px' }}>接口调用失败</h3>
          <p style={{ margin: 0, fontSize: '14px' }}>错误信息: {apiResponse.error}</p>
        </div>
      )}

      {/* 笔记列表 - 只有在没有API数据时才显示 */}
      {(!apiResponse || !apiResponse.data || !Array.isArray(apiResponse.data) || apiResponse.data.length === 0) && (
        <div className="notes-list">
          {notes.length > 0 ? (
            notes.map((note) => (
              <div key={note.noteId} className="note-item" data-type={note.title?.endsWith('.txt') ? 'file' : 'text'}>
                <div className="note-content" onClick={() => handleNoteClick(note)}>
                  <div className="note-header">
                    <div className="note-icon">
                      {note.title?.endsWith('.txt') ? '📄' : '📝'}
                    </div>
                    <div className="note-info">
                      <h3 className="note-title">{note.title}</h3>
                      <div className="note-meta">
                        <span className="note-time">{note.uploadTime}</span>
                        <span 
                          className="note-status"
                          style={{ color: getStatusColor(note.status) }}
                        >
                          {getStatusText(note.status)}
                        </span>
                        {note.knowledgePointCount > 0 && (
                          <span className="note-knowledge-count">
                            {note.knowledgePointCount}个知识点
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                  <div className="note-preview">
                    <p className="file-description">
                      {note.title?.endsWith('.txt') ? 'TXT文件笔记' : '文字笔记'}
                      {note.tags && note.tags.length > 0 && (
                        <span className="note-tags">
                          {note.tags.map(tag => `#${tag}`).join(' ')}
                        </span>
                      )}
                    </p>
                  </div>
                </div>
                
                <div className="note-actions">
                  {/* 编辑按钮 - 只对文字笔记显示 */}
                  {note.title && !note.title.endsWith('.txt') && (
                    <button 
                      className="note-action-btn edit-btn"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleEditNote(note);
                      }}
                    >
                      编辑
                    </button>
                  )}
                  <button 
                    className="note-action-btn delete-btn"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDeleteNote(note.noteId);
                    }}
                  >
                    删除
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="empty-state">
              <div className="empty-icon">📝</div>
              <div className="empty-text">还没有任何笔记</div>
              <div className="empty-subtext">点击右下角的加号开始创建您的第一条笔记</div>
            </div>
          )}
        </div>
      )}

      {/* 创建按钮 */}
      <div className="create-button-container">
        <button 
          className="create-button"
          onClick={() => setShowCreateMenu(!showCreateMenu)}
          disabled={uploading}
        >
          <span className={`create-icon ${showCreateMenu ? 'rotated' : ''}`}>
            {uploading ? '⏳' : '+'}
          </span>
        </button>
        
        {/* 创建菜单 */}
        {showCreateMenu && !uploading && (
          <div className="create-menu">
            <div className="create-menu-item" onClick={handleCreateText}>
              <span className="menu-icon">📝</span>
              <span>文字笔记</span>
            </div>
            <div className="create-menu-item" onClick={handleUploadFile}>
              <span className="menu-icon">📄</span>
              <span>上传TXT文件</span>
            </div>
          </div>
        )}
      </div>

      {/* 隐藏的文件输入框 */}
      <input
        ref={fileInputRef}
        type="file"
        accept=".txt"
        onChange={handleFileChange}
        style={{ display: 'none' }}
      />

      {/* 文字编辑器弹窗 */}
      {showTextEditor && (
        <div className="text-editor-overlay">
          <div className="text-editor">
            <div className="editor-header">
              <h3>{editingNote ? '编辑笔记' : '新建笔记'}</h3>
              <button 
                className="close-btn"
                onClick={() => {
                  setShowTextEditor(false);
                  setEditingNote(null);
                }}
              >
                ×
              </button>
            </div>
            
            <div className="editor-content">
              <input
                type="text"
                placeholder="请输入标题"
                value={textTitle}
                onChange={(e) => setTextTitle(e.target.value)}
                className="title-input"
                disabled={uploading}
              />
              <textarea
                placeholder="请输入内容"
                value={textContent}
                onChange={(e) => setTextContent(e.target.value)}
                className="content-textarea"
                rows={10}
                disabled={uploading}
              />
            </div>
            
            <div className="editor-actions">
              <button 
                className="cancel-btn"
                onClick={() => {
                  setShowTextEditor(false);
                  setEditingNote(null);
                }}
                disabled={uploading}
              >
                取消
              </button>
              <button 
                className="save-btn" 
                onClick={handleSaveText}
                disabled={uploading}
              >
                {uploading ? '保存中...' : '保存'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 点击遮罩关闭菜单 */}
      {showCreateMenu && (
        <div 
          className="menu-overlay"
          onClick={() => setShowCreateMenu(false)}
        />
      )}
    </div>
  );
};

export default Notes;
