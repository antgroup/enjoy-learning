import React, { useState, useEffect, useCallback } from 'react';
import KnowledgeMap from '../../components/KnowledgeMap/index';
import KnowledgeTree from '../../components/KnowledgeTree';
import KnowledgeGlobe from '../../components/KnowledgeGlobe';
import './index.css';

const KnowledgeOrganize: React.FC = () => {
  const [viewMode, setViewMode] = useState('tree'); // tree/globe/map
  const [currentViewTitle, setCurrentViewTitle] = useState('知识树');

  const handleViewModeChange = useCallback(
    (e: CustomEvent<{ viewMode: string }>) => {
      setViewMode(e.detail.viewMode);
    },
    []
  );

  useEffect(() => {
    const listener = (e: Event) => handleViewModeChange(e as CustomEvent);
    window.addEventListener('viewModeChange', listener);
    return () => window.removeEventListener('viewModeChange', listener);
  }, [handleViewModeChange]);

  // 点击外部关闭下拉菜单
  useEffect(() => {
    const handleClickOutside = () => {
      document.querySelector('.view-dropdown')?.classList.remove('show');
    };
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  useEffect(() => {
    const handleViewModeChange = (e: CustomEvent) => {
      setCurrentViewTitle(
        e.detail.viewMode === 'tree'
          ? '知识树'
          : e.detail.viewMode === 'globe'
          ? '知识球'
          : '知识地图'
      );
    };

    window.addEventListener(
      'viewModeChange',
      handleViewModeChange as EventListener
    );
    return () => {
      window.removeEventListener(
        'viewModeChange',
        handleViewModeChange as EventListener
      );
    };
  }, []);

  const renderContent = () => {
    switch (viewMode) {
      case 'tree':
        return <KnowledgeTree />;
      case 'globe':
        return <KnowledgeGlobe />;
      case 'map':
        return <KnowledgeMap />;
      default:
        return <KnowledgeTree />;
    }
  };

  return (
    <div className="knowledge-organize">
      {/* Header */}
      <div className="navbar">
        <div className="navbar-title">{currentViewTitle}</div>
        <div className="navbar-actions">
          <div className="view-switch-container">
            <button
              className="btn btn-sm btn-outline"
              id="viewSwitchBtn"
              onClick={(e) => {
                e.stopPropagation();
                const dropdown = document.querySelector('.view-dropdown');
                dropdown?.classList.toggle('show');
              }}
            >
              <span>视图切换</span>
              <span style={{ marginLeft: '5px', display: 'inline-block' }}>
                ▼
              </span>
            </button>
            <div className="view-dropdown">
              <div
                className="dropdown-item"
                onClick={() => {
                  window.dispatchEvent(
                    new CustomEvent('viewModeChange', {
                      detail: { viewMode: 'tree' },
                    })
                  );
                  document
                    .querySelector('.view-dropdown')
                    ?.classList.remove('show');
                }}
              >
                知识树视图
              </div>
              <div
                className="dropdown-item"
                onClick={() => {
                  window.dispatchEvent(
                    new CustomEvent('viewModeChange', {
                      detail: { viewMode: 'globe' },
                    })
                  );
                  document
                    .querySelector('.view-dropdown')
                    ?.classList.remove('show');
                }}
              >
                知识球视图
              </div>
              <div
                className="dropdown-item"
                onClick={() => {
                  window.dispatchEvent(
                    new CustomEvent('viewModeChange', {
                      detail: { viewMode: 'map' },
                    })
                  );
                  document
                    .querySelector('.view-dropdown')
                    ?.classList.remove('show');
                }}
              >
                知识地图视图
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="knowledge-content">
        {renderContent()}
      </div>
    </div>
  );
};

export default KnowledgeOrganize;
