import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { userApi, tokenUtils, UserInfo } from '../../api';
import './index.css';

const Profile: React.FC = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState<UserInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 静态学习数据
  const learningStats = {
    totalStudyTime: 1248, // 总学习时长（分钟）
    knowledgePoints: 156, // 知识点数量
    notesCount: 42, // 笔记数量
    completionRate: 78, // 完成率
    studyDays: 23, // 学习天数
    weeklyGoal: 300, // 周目标（分钟）
    weeklyProgress: 245, // 本周进度（分钟）
    dailyAverage: 54, // 日均学习时长（分钟）
    streak: 7, // 连续学习天数
  };

  useEffect(() => {
    // 检查是否已登录
    if (!tokenUtils.hasToken()) {
      navigate('/login');
      return;
    }

    // 获取用户信息
    fetchUserInfo();
  }, [navigate]);

  const fetchUserInfo = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await userApi.getUserInfo();

      if (response.code === '00000' && response.data) {
        setUser(response.data);
      } else {
        // 如果是登录错误（token过期），跳转到登录页
        if (response.code === 'A0200') {
          tokenUtils.removeToken();
          navigate('/login');
          return;
        }
        throw new Error(response.errorMessage || '获取用户信息失败');
      }
    } catch (error: any) {
      console.error('获取用户信息失败:', error);
      setError(error.message || '获取用户信息失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      // 调用登出API
      await userApi.logout();
    } catch (error) {
      console.error('登出失败:', error);
    } finally {
      // 无论API调用是否成功，都清除本地token并跳转
      tokenUtils.removeToken();
      navigate('/login');
    }
  };

  // 格式化时间显示
  const formatTime = (minutes: number): string => {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0) {
      return `${hours}h ${mins}m`;
    }
    return `${mins}m`;
  };

  // 圆形进度条组件
  const CircularProgress: React.FC<{ percentage: number; size?: number; strokeWidth?: number; color?: string }> = ({
    percentage,
    size = 120,
    strokeWidth = 8,
    color = '#4CAF50'
  }) => {
    const radius = (size - strokeWidth) / 2;
    const circumference = radius * 2 * Math.PI;
    const strokeDasharray = `${circumference} ${circumference}`;
    const strokeDashoffset = circumference - (percentage / 100) * circumference;

    return (
      <div className="circular-progress" style={{ width: size, height: size }}>
        <svg width={size} height={size} className="circular-progress-svg">
          {/* 背景圆环 */}
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="#e6e6e6"
            strokeWidth={strokeWidth}
            fill="transparent"
          />
          {/* 进度圆环 */}
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke={color}
            strokeWidth={strokeWidth}
            fill="transparent"
            strokeDasharray={strokeDasharray}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
            className="circular-progress-circle"
            style={{
              transition: 'stroke-dashoffset 0.5s ease-in-out',
              transform: 'rotate(-90deg)',
              transformOrigin: '50% 50%'
            }}
          />
        </svg>
        <div className="circular-progress-text">
          <span className="percentage">{percentage}%</span>
        </div>
      </div>
    );
  };

  if (loading) {
    return (
      <div className="profile-container">
        <div className="loading-state">
          <div className="spinner"></div>
          <div>加载用户信息中...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="profile-container">
        <div className="error-state">
          <div className="error-icon">⚠️</div>
          <div className="error-message">{error}</div>
          <button className="retry-button" onClick={fetchUserInfo}>
            重试
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-container">
      {/* 用户信息卡片 */}
      <div className="user-card">
        <div className="user-avatar">
          <img
            src={'/default-avatar.png'}
            alt="用户头像"
            onError={(e) => {
              (e.target as HTMLImageElement).src = '';
            }}
          />
        </div>
        <div className="user-info">
          <h2 className="user-name">{user?.username || '用户'}</h2>
          <p className="user-email">{user?.account || 'user@example.com'}</p>
        </div>
      </div>

      {/* 学习统计概览 */}
      <div className="stats-section">
        <h3 className="section-title">学习统计</h3>

        {/* 主要统计数据 */}
        <div className="main-stats">
          <div className="progress-section">
            <div className="progress-chart">
              <CircularProgress
                percentage={Math.round((learningStats.weeklyProgress / learningStats.weeklyGoal) * 100)}
                size={140}
                strokeWidth={10}
                color="#4CAF50"
              />
              <div className="progress-info">
                <div className="progress-label">本周目标</div>
                <div className="progress-detail">
                  {formatTime(learningStats.weeklyProgress)} / {formatTime(learningStats.weeklyGoal)}
                </div>
              </div>
            </div>
          </div>

          <div className="stats-grid">
            <div className="stat-item">
              <div className="stat-icon">📚</div>
              <div className="stat-content">
                <div className="stat-number">{learningStats.knowledgePoints}</div>
                <div className="stat-label">知识点</div>
              </div>
            </div>
            <div className="stat-item">
              <div className="stat-icon">📝</div>
              <div className="stat-content">
                <div className="stat-number">{learningStats.notesCount}</div>
                <div className="stat-label">笔记数</div>
              </div>
            </div>
            <div className="stat-item">
              <div className="stat-icon">⏱️</div>
              <div className="stat-content">
                <div className="stat-number">{formatTime(learningStats.totalStudyTime)}</div>
                <div className="stat-label">总时长</div>
              </div>
            </div>
            <div className="stat-item">
              <div className="stat-icon">🔥</div>
              <div className="stat-content">
                <div className="stat-number">{learningStats.streak}</div>
                <div className="stat-label">连续天数</div>
              </div>
            </div>
          </div>
        </div>

        {/* 详细统计 */}
        <div className="detailed-stats">
          <div className="stat-row">
            <div className="stat-row-label">
              <span className="stat-row-icon">📊</span>
              完成率
            </div>
            <div className="stat-row-value">
              <div className="progress-bar">
                <div
                  className="progress-fill"
                  style={{ width: `${learningStats.completionRate}%` }}
                ></div>
              </div>
              <span className="progress-text">{learningStats.completionRate}%</span>
            </div>
          </div>

          <div className="stat-row">
            <div className="stat-row-label">
              <span className="stat-row-icon">📅</span>
              学习天数
            </div>
            <div className="stat-row-value">
              <span className="stat-value">{learningStats.studyDays} 天</span>
            </div>
          </div>

          <div className="stat-row">
            <div className="stat-row-label">
              <span className="stat-row-icon">⏰</span>
              日均时长
            </div>
            <div className="stat-row-value">
              <span className="stat-value">{formatTime(learningStats.dailyAverage)}</span>
            </div>
          </div>
        </div>
      </div>

      {/* 用户详细信息 */}
      <div className="stats-section">
        <h3 className="section-title">账户信息</h3>
        <div className="user-details">
          <div className="detail-item">
            <div className="detail-label">用户ID</div>
            <div className="detail-value">{user?.id || '-'}</div>
          </div>
          <div className="detail-item">
            <div className="detail-label">账户状态</div>
            <div className="detail-value">
              <span className={`status-badge ${user?.status === 'active' ? 'active' : 'inactive'}`}>
                {user?.status === 'active' ? '正常' : '未知'}
              </span>
            </div>
          </div>
          <div className="detail-item">
            <div className="detail-label">注册时间</div>
            <div className="detail-value">{user?.createdAt || '-'}</div>
          </div>
          <div className="detail-item">
            <div className="detail-label">最后更新</div>
            <div className="detail-value">{user?.updatedAt || '-'}</div>
          </div>
        </div>
      </div>

      {/* 功能菜单 */}
      <div className="menu-section">
        <h3 className="section-title">功能菜单</h3>
        <div className="menu-list">
          <div className="menu-item">
            <div className="menu-icon">📚</div>
            <div className="menu-content">
              <div className="menu-title">学习记录</div>
              <div className="menu-subtitle">查看详细的学习历史</div>
            </div>
            <div className="menu-arrow">›</div>
          </div>

          <div className="menu-item">
            <div className="menu-icon">🎯</div>
            <div className="menu-content">
              <div className="menu-title">学习目标</div>
              <div className="menu-subtitle">设置和管理学习计划</div>
            </div>
            <div className="menu-arrow">›</div>
          </div>

          <div className="menu-item">
            <div className="menu-icon">🏆</div>
            <div className="menu-content">
              <div className="menu-title">成就徽章</div>
              <div className="menu-subtitle">查看获得的学习成就</div>
            </div>
            <div className="menu-arrow">›</div>
          </div>

          <div className="menu-item">
            <div className="menu-icon">⚙️</div>
            <div className="menu-content">
              <div className="menu-title">设置</div>
              <div className="menu-subtitle">个人信息和偏好设置</div>
            </div>
            <div className="menu-arrow">›</div>
          </div>
        </div>
      </div>

      {/* 退出登录按钮 */}
      <div className="logout-section">
        <button className="logout-button" onClick={handleLogout}>
          退出登录
        </button>
      </div>
    </div>
  );
};

export default Profile;
