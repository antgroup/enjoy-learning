import React, { useState } from 'react';
import './index.css';

const Community: React.FC = () => {
  const [activeTab, setActiveTab] = useState('posts'); // posts, groups, events
  const [selectedCategory, setSelectedCategory] = useState('全部');

  const categories = ['全部', '前端开发', '后端技术', '数据科学', 'DevOps', '职业发展'];

  const posts = [
    {
      id: 1,
      title: 'React 18 新特性深度解析',
      author: '小三',
      avatar: '👨‍💻',
      category: '前端开发',
      likes: 128,
      comments: 45,
      time: '2小时前',
      content: 'React 18 带来了许多令人兴奋的新特性，包括并发渲染、自动批处理等...',
      tags: ['React', 'JavaScript', '前端']
    },
    {
      id: 2,
      title: 'Docker 容器化最佳实践分享',
      author: '小四',
      avatar: '👩‍💻',
      category: 'DevOps',
      likes: 89,
      comments: 23,
      time: '4小时前',
      content: '在生产环境中使用Docker的一些经验总结和最佳实践...',
      tags: ['Docker', 'DevOps', '容器化']
    },
    {
      id: 3,
      title: '机器学习入门指南',
      author: '小五',
      avatar: '🧑‍🔬',
      category: '数据科学',
      likes: 156,
      comments: 67,
      time: '1天前',
      content: '从零开始学习机器学习，包括基础概念、常用算法和实践项目...',
      tags: ['机器学习', 'Python', '数据科学']
    }
  ];

  const groups = [
    {
      id: 1,
      name: 'React 开发者社区',
      description: '专注于React技术交流与分享',
      members: 1234,
      posts: 567,
      avatar: '⚛️',
      isJoined: true
    },
    {
      id: 2,
      name: 'DevOps 实践小组',
      description: '分享DevOps工具和最佳实践',
      members: 890,
      posts: 234,
      avatar: '🔧',
      isJoined: false
    },
    {
      id: 3,
      name: '数据科学研究院',
      description: '探讨数据科学前沿技术',
      members: 2156,
      posts: 1023,
      avatar: '📊',
      isJoined: true
    }
  ];

  const events = [
    {
      id: 1,
      title: 'React 技术分享会',
      date: '2024-01-15',
      time: '19:00-21:00',
      location: '线上',
      participants: 156,
      maxParticipants: 200,
      status: 'upcoming',
      organizer: '前端技术社区'
    },
    {
      id: 2,
      title: 'Docker 实战工作坊',
      date: '2024-01-20',
      time: '14:00-17:00',
      location: '北京',
      participants: 45,
      maxParticipants: 50,
      status: 'upcoming',
      organizer: 'DevOps 小组'
    }
  ];

  const filteredPosts = selectedCategory === '全部'
    ? posts
    : posts.filter(post => post.category === selectedCategory);

  const renderPosts = () => (
    <div className="posts-section">
      <div className="category-filter">
        {categories.map(category => (
          <button
            key={category}
            className={`filter-chip ${selectedCategory === category ? 'active' : ''}`}
            onClick={() => setSelectedCategory(category)}
          >
            {category}
          </button>
        ))}
      </div>

      <div className="posts-list">
        {filteredPosts.map(post => (
          <div key={post.id} className="post-card">
            <div className="post-header">
              <div className="author-info">
                <span className="author-avatar">{post.avatar}</span>
                <div>
                  <div className="author-name">{post.author}</div>
                  <div className="post-time">{post.time}</div>
                </div>
              </div>
              <span className="category-tag">{post.category}</span>
            </div>

            <h3 className="post-title">{post.title}</h3>
            <p className="post-content">{post.content}</p>

            <div className="post-tags">
              {post.tags.map(tag => (
                <span key={tag} className="tag">#{tag}</span>
              ))}
            </div>

            <div className="post-actions">
              <button className="action-btn">
                <span>👍</span>
                <span>{post.likes}</span>
              </button>
              <button className="action-btn">
                <span>💬</span>
                <span>{post.comments}</span>
              </button>
              <button className="action-btn">
                <span>🔗</span>
                <span>分享</span>
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );

  const renderGroups = () => (
    <div className="groups-section">
      <div className="groups-list">
        {groups.map(group => (
          <div key={group.id} className="group-card">
            <div className="group-avatar">{group.avatar}</div>
            <div className="group-info">
              <h3 className="group-name">{group.name}</h3>
              <p className="group-description">{group.description}</p>
              <div className="group-stats">
                <span>{group.members} 成员</span>
                <span>{group.posts} 帖子</span>
              </div>
            </div>
            <button className={`join-btn ${group.isJoined ? 'joined' : ''}`}>
              {group.isJoined ? '已加入' : '加入'}
            </button>
          </div>
        ))}
      </div>
    </div>
  );

  const renderEvents = () => (
    <div className="events-section">
      <div className="events-list">
        {events.map(event => (
          <div key={event.id} className="event-card">
            <div className="event-date">
              <div className="date-day">{event.date.split('-')[2]}</div>
              <div className="date-month">{event.date.split('-')[1]}月</div>
            </div>
            <div className="event-info">
              <h3 className="event-title">{event.title}</h3>
              <div className="event-details">
                <div className="event-time">🕐 {event.time}</div>
                <div className="event-location">📍 {event.location}</div>
                <div className="event-organizer">👥 {event.organizer}</div>
              </div>
              <div className="event-participants">
                {event.participants}/{event.maxParticipants} 人参加
              </div>
            </div>
            <button className="participate-btn">报名参加</button>
          </div>
        ))}
      </div>
    </div>
  );

  return (
    <div className="community-container">
      {/* Header */}
      <div className="community-header">
        <h1>学习社区</h1>
        <button className="create-btn">+ 发布内容</button>
      </div>

      {/* Tab Navigation */}
      <div className="tab-navigation">
        <button
          className={`tab-btn ${activeTab === 'posts' ? 'active' : ''}`}
          onClick={() => setActiveTab('posts')}
        >
          📝 讨论
        </button>
        <button
          className={`tab-btn ${activeTab === 'groups' ? 'active' : ''}`}
          onClick={() => setActiveTab('groups')}
        >
          👥 小组
        </button>
        <button
          className={`tab-btn ${activeTab === 'events' ? 'active' : ''}`}
          onClick={() => setActiveTab('events')}
        >
          📅 活动
        </button>
      </div>

      {/* Content */}
      <div className="tab-content">
        {activeTab === 'posts' && renderPosts()}
        {activeTab === 'groups' && renderGroups()}
        {activeTab === 'events' && renderEvents()}
      </div>
    </div>
  );
};

export default Community;
