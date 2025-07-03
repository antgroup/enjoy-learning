import React from 'react';
import { DocumentModalProps } from './types';

const DocumentModal: React.FC<DocumentModalProps> = ({
  show,
  onClose,
  documentsLoading,
  apiResponse,
  associating,
  onDocumentSelect,
  handleClickDetails
}) => {
  if (!show) return null;

  return (
    <div className="document-modal-overlay">
      <div className="document-modal">
        <div className="document-modal-header">
          <h3>选择要关联的文档</h3>
          <button 
            className="document-modal-close"
            onClick={onClose}
          >
            ×
          </button>
        </div>
        <div className="document-modal-content">
          {documentsLoading ? (
            <div className="document-loading">
              <div className="spinner"></div>
              <div>加载中...</div>
            </div>
          ) : (
            <>
              {/* 简化的文档列表展示 */}
              {apiResponse && apiResponse.data && Array.isArray(apiResponse.data) && (
                <div className="notes-section">
                  <h4 className="notes-section-title">
                    文档列表 (共 {apiResponse.data.length} 条记录)
                  </h4>
                  <p style={{ fontSize: '14px', color: '#666', marginBottom: '16px' }}>
                    点击文档将其关联到知识树，AI会自动分析并更新知识树结构
                  </p>
                  
                  {apiResponse.data.length > 0 ? (
                    <div className="simple-notes-list">
                      {apiResponse.data.map((note: any, index: number) => (
                        <div 
                          key={note.id || index} 
                          className={`simple-note-item ${associating ? 'disabled' : ''}`}
                          onClick={() => !associating && onDocumentSelect(note)}
                          style={{ 
                            opacity: associating ? 0.6 : 1,
                            cursor: associating ? 'not-allowed' : 'pointer'
                          }}
                        >
                          <div className="simple-note-title">
                            {note.title || '无标题'}
                          </div>
                          <div className="simple-note-date">
                            创建时间: {note.createdAt || '未知'}
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="empty-notes">
                      <div className="empty-icon">📝</div>
                      <div className="empty-text">暂无文档数据</div>
                    </div>
                  )}
                </div>
              )}
              
              {/* 如果有错误，显示错误信息 */}
              {apiResponse && apiResponse.error && (
                <div className="error-section">
                  <h4 className="error-title">接口调用失败</h4>
                  <p className="error-message">错误信息: {apiResponse.error}</p>
                </div>
              )}

              {/* 如果没有API响应数据 */}
              {!apiResponse && (
                <div className="empty-notes">
                  <div className="empty-icon">📄</div>
                  <div className="empty-text">暂无文档数据</div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default DocumentModal;
