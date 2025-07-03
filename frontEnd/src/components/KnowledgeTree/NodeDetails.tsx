import React from 'react';
import { NodeDetailsProps } from './types';

const NodeDetails: React.FC<NodeDetailsProps> = ({ onClose, onDetailsClick }) => {
  return (
    <div className="node-details" onClick={(e) => e.stopPropagation()}>
      <div className="node-details-header">
        <h2 className="node-details-title">节点名称</h2>
        <button className="node-details-close" onClick={onClose}>×</button>
      </div>
      {/* 添加详情按钮 - 只有当onDetailsClick存在时才显示 */}
        {onDetailsClick && (
          <div className="node-details-actions">
            <button 
              className="details-button"
              onClick={() => {
                onDetailsClick();
                onClose();
              }}
              title="查看详细信息"
            >
              查看详情
            </button>
          </div>
        )}
      <div className="node-details-content">
        <div className="node-details-level">第 0 层</div>
        <div className="auto-generated">手动创建</div>
        <p className="node-details-description">节点描述</p>
        <div className="node-details-progress">
          <div className="progress-label">
            <span>学习进度</span>
            <span>0%</span>
          </div>
          <div className="progress-bar">
            <div className="progress-value"></div>
          </div>
        </div>
        <div className="notes-count">关联笔记: 0 篇</div>
        <div className="node-details-keywords-label">关键词</div>
        <div className="node-keywords">
          <span className="keyword-tag">暂无关键词</span>
        </div>
        
      </div>
    </div>
  );
};

export default NodeDetails;
