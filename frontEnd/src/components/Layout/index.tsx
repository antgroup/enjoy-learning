import React from 'react';
import { Outlet, useLocation, Link } from 'react-router-dom';
import './index.css';

const Layout: React.FC = () => {
  const location = useLocation();

  const navItems = [
    { path: '/app/knowledge-map', icon: '🌍', label: '知识地图' },
    { path: '/app/notes', icon: '📝', label: '笔记' },
    { path: '/app/profile', icon: '👤', label: '我的' },
  ];

  return (
    <div className="layout">
      {/* 主内容区域 */}
      <main className="main-content">
        <Outlet />
      </main>

      {/* 底部导航栏 */}
      <nav className="bottom-nav">
        {navItems.map((item) => (
          <Link
            key={item.path}
            to={item.path}
            className={`nav-item ${location.pathname === item.path ? 'active' : ''}`}
          >
            <div className="nav-icon">{item.icon}</div>
            <div className="nav-label">{item.label}</div>
          </Link>
        ))}
      </nav>
    </div>
  );
};

export default Layout;
