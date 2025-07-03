import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { tokenUtils } from '../../api';

const IndexRoute: React.FC = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // 检查用户是否已登录
    if (!tokenUtils.hasToken()) {
      navigate('/login');
      return;
    }

    // 检查是否是第一次访问
    const hasVisited = localStorage.getItem('hasVisited');
    
    if (!hasVisited) {
      // 第一次访问，标记已访问并跳转到引导页
      localStorage.setItem('hasVisited', 'true');
      navigate('/guide');
    } else {
      // 不是第一次访问，直接跳转到知识地图
      navigate('/app/knowledge-map');
    }
  }, [navigate]);

  // 显示加载状态
  return (
    <div style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      height: '100vh',
      backgroundColor: '#f5f5f5'
    }}>
      <div style={{
        textAlign: 'center',
        color: '#666'
      }}>
        <div style={{
          fontSize: '20px',
          marginBottom: '10px'
        }}>
          🚀
        </div>
        <div>正在加载...</div>
      </div>
    </div>
  );
};

export default IndexRoute;
