import React, { useState } from 'react';
import './index.css';

const Review: React.FC = () => {
  const [selectedSubject, setSelectedSubject] = useState('全部');
  const [reviewMode, setReviewMode] = useState('flashcard'); // flashcard, quiz, practice

  const subjects = ['全部', '前端开发', '后端技术', '数据科学', 'DevOps'];
  
  const reviewItems = [
    {
      id: 1,
      title: 'React Hooks 基础',
      subject: '前端开发',
      difficulty: '中等',
      lastReview: '2天前',
      nextReview: '今天',
      mastery: 75,
      type: 'concept'
    },
    {
      id: 2,
      title: 'JavaScript 异步编程',
      subject: '前端开发',
      difficulty: '困难',
      lastReview: '1周前',
      nextReview: '今天',
      mastery: 60,
      type: 'concept'
    },
    {
      id: 3,
      title: 'Docker 容器化部署',
      subject: 'DevOps',
      difficulty: '中等',
      lastReview: '3天前',
      nextReview: '明天',
      mastery: 80,
      type: 'practice'
    }
  ];

  const filteredItems = selectedSubject === '全部' 
    ? reviewItems 
    : reviewItems.filter(item => item.subject === selectedSubject);

  const getMasteryColor = (mastery: number) => {
    if (mastery >= 80) return '#52c41a';
    if (mastery >= 60) return '#faad14';
    return '#ff4d4f';
  };

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case '简单': return '#52c41a';
      case '中等': return '#faad14';
      case '困难': return '#ff4d4f';
      default: return '#1890ff';
    }
  };

  return (
    <div className="review-container">
      {/* Header */}
      <div className="review-header">
        <h1>复习计划</h1>
        <div className="review-stats">
          <div className="stat-item">
            <span className="stat-number">12</span>
            <span className="stat-label">今日待复习</span>
          </div>
          <div className="stat-item">
            <span className="stat-number">85%</span>
            <span className="stat-label">平均掌握度</span>
          </div>
        </div>
      </div>

      {/* Mode Selector */}
      <div className="mode-selector">
        <button 
          className={`mode-btn ${reviewMode === 'flashcard' ? 'active' : ''}`}
          onClick={() => setReviewMode('flashcard')}
        >
          📚 闪卡复习
        </button>
        <button 
          className={`mode-btn ${reviewMode === 'quiz' ? 'active' : ''}`}
          onClick={() => setReviewMode('quiz')}
        >
          🧠 测验模式
        </button>
        <button 
          className={`mode-btn ${reviewMode === 'practice' ? 'active' : ''}`}
          onClick={() => setReviewMode('practice')}
        >
          💻 实践练习
        </button>
      </div>

      {/* Subject Filter */}
      <div className="subject-filter">
        {subjects.map(subject => (
          <button
            key={subject}
            className={`filter-chip ${selectedSubject === subject ? 'active' : ''}`}
            onClick={() => setSelectedSubject(subject)}
          >
            {subject}
          </button>
        ))}
      </div>

      {/* Review Items */}
      <div className="review-items">
        {filteredItems.map(item => (
          <div key={item.id} className="review-item">
            <div className="item-header">
              <h3>{item.title}</h3>
              <span 
                className="difficulty-badge"
                style={{ backgroundColor: getDifficultyColor(item.difficulty) }}
              >
                {item.difficulty}
              </span>
            </div>
            
            <div className="item-info">
              <span className="subject-tag">{item.subject}</span>
              <span className="review-time">下次复习: {item.nextReview}</span>
            </div>
            
            <div className="mastery-section">
              <div className="mastery-label">
                <span>掌握度</span>
                <span>{item.mastery}%</span>
              </div>
              <div className="mastery-bar">
                <div 
                  className="mastery-progress"
                  style={{ 
                    width: `${item.mastery}%`,
                    backgroundColor: getMasteryColor(item.mastery)
                  }}
                />
              </div>
            </div>
            
            <div className="item-actions">
              <button className="btn btn-outline">查看详情</button>
              <button className="btn btn-primary">开始复习</button>
            </div>
          </div>
        ))}
      </div>

      {/* Quick Actions */}
      <div className="quick-actions">
        <button className="quick-action-btn">
          <span className="action-icon">🎯</span>
          <span>智能复习</span>
        </button>
        <button className="quick-action-btn">
          <span className="action-icon">📊</span>
          <span>复习统计</span>
        </button>
        <button className="quick-action-btn">
          <span className="action-icon">⚙️</span>
          <span>复习设置</span>
        </button>
      </div>
    </div>
  );
};

export default Review;
