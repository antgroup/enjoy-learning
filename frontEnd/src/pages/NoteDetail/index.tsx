import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { notesApi, NoteDetail } from '../../api';
import './index.css';

const NoteDetailPage: React.FC = () => {
  const { noteId } = useParams<{ noteId: string }>();
  const navigate = useNavigate();
  const [noteDetail, setNoteDetail] = useState<NoteDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 获取当前用户ID
  const getCurrentUserId = (): string => {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      try {
        const parsed = JSON.parse(userInfo);
        return parsed.userId || 'test_user_id';
      } catch (error) {
        console.error('解析用户信息失败:', error);
      }
    }
    return 'test_user_id';
  };

  // 获取笔记详情
  const fetchNoteDetail = async () => {
    if (!noteId) {
      setError('笔记ID不存在');
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);
      
      const userId = getCurrentUserId();
      const response = await notesApi.getNoteDetail(noteId, userId);
      
      if (response.code === '00000' && response.data) {
        setNoteDetail(response.data);
      } else {
        setError(response.errorMessage || '获取笔记详情失败');
      }
    } catch (error: any) {
      console.error('获取笔记详情失败:', error);
      setError(error.message || '获取笔记详情失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNoteDetail();
  }, [noteId]);

  const handleBack = () => {
    navigate(-1);
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

  if (loading) {
    return (
      <div className="note-detail-container">
        <div className="note-detail-header">
          <button className="back-btn" onClick={handleBack}>
            ← 返回
          </button>
        </div>
        <div className="loading-state">
          <div className="spinner"></div>
          <div>加载中...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="note-detail-container">
        <div className="note-detail-header">
          <button className="back-btn" onClick={handleBack}>
            ← 返回
          </button>
        </div>
        <div className="error-state">
          <div className="error-icon">❌</div>
          <div className="error-text">{error}</div>
          <button className="retry-btn" onClick={fetchNoteDetail}>
            重试
          </button>
        </div>
      </div>
    );
  }

  if (!noteDetail) {
    return (
      <div className="note-detail-container">
        <div className="note-detail-header">
          <button className="back-btn" onClick={handleBack}>
            ← 返回
          </button>
        </div>
        <div className="error-state">
          <div className="error-icon">📝</div>
          <div className="error-text">笔记不存在</div>
        </div>
      </div>
    );
  }

  return (
    <div className="note-detail-container">
      {/* 头部导航 */}
      <div className="note-detail-header">
        <button className="back-btn" onClick={handleBack}>
          ← 返回
        </button>
        <div className="header-actions">
          <span 
            className="status-badge"
            style={{ backgroundColor: getStatusColor(noteDetail.status) }}
          >
            {getStatusText(noteDetail.status)}
          </span>
        </div>
      </div>

      {/* 笔记信息 */}
      <div className="note-info-section">
        <h1 className="note-title">{noteDetail.title}</h1>
        <div className="note-meta">
          <span className="meta-item">
            📅 {noteDetail.uploadTime}
          </span>
          {noteDetail.analysisTime && (
            <span className="meta-item">
              🔍 分析时间：{noteDetail.analysisTime}
            </span>
          )}
          {noteDetail.knowledgePointCount > 0 && (
            <span className="meta-item">
              💡 {noteDetail.knowledgePointCount}个知识点
            </span>
          )}
        </div>
        
        {/* 标签 */}
        {noteDetail.tags && noteDetail.tags.length > 0 && (
          <div className="note-tags">
            {noteDetail.tags.map((tag, index) => (
              <span key={index} className="tag">
                #{tag}
              </span>
            ))}
          </div>
        )}
      </div>

      {/* 笔记内容 */}
      <div className="note-content-section">
        <h2 className="section-title">笔记内容</h2>
        <div className="markdown-content">
          <ReactMarkdown 
            remarkPlugins={[remarkGfm]}
            components={{
              // 自定义渲染组件
              h1: ({children}) => <h1 className="md-h1">{children}</h1>,
              h2: ({children}) => <h2 className="md-h2">{children}</h2>,
              h3: ({children}) => <h3 className="md-h3">{children}</h3>,
              h4: ({children}) => <h4 className="md-h4">{children}</h4>,
              h5: ({children}) => <h5 className="md-h5">{children}</h5>,
              h6: ({children}) => <h6 className="md-h6">{children}</h6>,
              p: ({children}) => <p className="md-p">{children}</p>,
              ul: ({children}) => <ul className="md-ul">{children}</ul>,
              ol: ({children}) => <ol className="md-ol">{children}</ol>,
              li: ({children}) => <li className="md-li">{children}</li>,
              blockquote: ({children}) => <blockquote className="md-blockquote">{children}</blockquote>,
              code: ({children, className}) => {
                const isInline = !className;
                return isInline ? 
                  <code className="md-code-inline">{children}</code> :
                  <code className={`md-code-block ${className}`}>{children}</code>;
              },
              pre: ({children}) => <pre className="md-pre">{children}</pre>,
              table: ({children}) => <table className="md-table">{children}</table>,
              thead: ({children}) => <thead className="md-thead">{children}</thead>,
              tbody: ({children}) => <tbody className="md-tbody">{children}</tbody>,
              tr: ({children}) => <tr className="md-tr">{children}</tr>,
              th: ({children}) => <th className="md-th">{children}</th>,
              td: ({children}) => <td className="md-td">{children}</td>,
              a: ({children, href}) => <a className="md-link" href={href} target="_blank" rel="noopener noreferrer">{children}</a>,
              img: ({src, alt}) => <img className="md-img" src={src} alt={alt} />,
              hr: () => <hr className="md-hr" />,
              strong: ({children}) => <strong className="md-strong">{children}</strong>,
              em: ({children}) => <em className="md-em">{children}</em>,
            }}
          >
            {noteDetail.rawContent}
          </ReactMarkdown>
        </div>
      </div>

      {/* 大纲信息 */}
      {noteDetail.outline && (
        <div className="outline-section">
          <h2 className="section-title">内容大纲</h2>
          <div className="outline-content">
            <h3 className="outline-title">{noteDetail.outline.title}</h3>
            <p className="outline-summary">{noteDetail.outline.summary}</p>
            {noteDetail.outline.mainPoints && noteDetail.outline.mainPoints.length > 0 && (
              <div className="main-points">
                <h4>主要观点：</h4>
                <ul>
                  {noteDetail.outline.mainPoints.map((point, index) => (
                    <li key={index}>{point}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        </div>
      )}

      {/* 知识点信息 */}
      {noteDetail.knowledgePoints && noteDetail.knowledgePoints.length > 0 && (
        <div className="knowledge-points-section">
          <h2 className="section-title">提取的知识点</h2>
          <div className="knowledge-points-grid">
            {noteDetail.knowledgePoints.map((point, index) => (
              <div key={index} className="knowledge-point-card">
                <div className="point-header">
                  <h3 className="point-name">{point.name}</h3>
                  <span className="point-category">{point.category}</span>
                </div>
                <p className="point-description">{point.description}</p>
                <div className="point-meta">
                  <span className="point-difficulty">
                    难度：{point.difficulty}
                  </span>
                  <span className="point-importance">
                    重要性：{point.importance}
                  </span>
                </div>
                {point.keywords && point.keywords.length > 0 && (
                  <div className="point-keywords">
                    {point.keywords.map((keyword, kidx) => (
                      <span key={kidx} className="keyword">
                        {keyword}
                      </span>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default NoteDetailPage;
