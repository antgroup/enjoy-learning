import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import { tokenUtils } from './api';

// 导入组件
import Layout from './components/Layout';

// 导入页面
import Login from './pages/Login';
import Profile from './pages/Profile';
import KnowledgeOrganize from './pages/KnowledgeOrganize';
import Notes from './pages/Notes';
import Guide from './pages/Guide';
import IndexRoute from './pages/IndexRoute';
import NoteDetail from './pages/NoteDetail';

// 路由保护组件
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const hasToken = tokenUtils.hasToken();
  
  if (!hasToken) {
    return <Navigate to="/login" replace />;
  }
  
  return <>{children}</>;
};

// 登录页面保护组件（已登录用户访问登录页时重定向）
const LoginRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const hasToken = tokenUtils.hasToken();
  
  if (hasToken) {
    return <Navigate to="/" replace />;
  }
  
  return <>{children}</>;
};

// Guide页面包装组件
const GuideWrapper: React.FC = () => {
  const navigate = useNavigate();
  
  const handleGuideComplete = () => {
    navigate('/app/knowledge-map');
  };
  
  return <Guide onComplete={handleGuideComplete} />;
};

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* 首页路由 - 判断是否第一次访问 */}
          <Route path="/" element={<IndexRoute />} />
          
          {/* 登录页面 */}
          <Route 
            path="/login" 
            element={
              <LoginRoute>
                <Login />
              </LoginRoute>
            } 
          />
          
          {/* 引导页面 - 独立页面，不使用Layout */}
          <Route 
            path="/guide" 
            element={
              <ProtectedRoute>
                <GuideWrapper />
              </ProtectedRoute>
            } 
          />
          
          {/* 笔记详情页面 - 独立页面，不使用Layout */}
          <Route 
            path="/note/:noteId" 
            element={
              <ProtectedRoute>
                <NoteDetail />
              </ProtectedRoute>
            } 
          />
          
          {/* 主应用布局 */}
          <Route 
            path="/app" 
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            {/* 嵌套路由 - 底部导航对应的页面 */}
            <Route path="knowledge-map" element={<KnowledgeOrganize />} />
            <Route path="notes" element={<Notes />} />
            <Route path="profile" element={<Profile />} />
            
            {/* 默认重定向到知识地图 */}
            <Route index element={<Navigate to="/app/knowledge-map" replace />} />
          </Route>
          
          {/* 为了兼容性，保留原有的直接路由 */}
          <Route path="/knowledge-map" element={<Navigate to="/app/knowledge-map" replace />} />
          <Route path="/notes" element={<Navigate to="/app/notes" replace />} />
          <Route path="/profile" element={<Navigate to="/app/profile" replace />} />
          
          {/* 404 页面或其他未匹配路由重定向 */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
