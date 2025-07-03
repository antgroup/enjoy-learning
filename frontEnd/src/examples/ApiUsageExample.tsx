import React, { useState } from 'react';
import { userApi, knowledgeMapApi } from '../api';

const ApiUsageExample: React.FC = () => {
  const [user, setUser] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    try {
      setLoading(true);
      const response = await userApi.login({
        account: 'testuser@example.com',
        password: 'password123',
        username: 'Test User'
      });
      
      if (response.code === '00000') {
        console.log('登录成功:', response.data);
        // 保存用户信息
        setUser(response.data);
      }
    } catch (error) {
      console.error('登录失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleGetUserInfo = async () => {
    try {
      setLoading(true);
      const response = await userApi.getUserInfo();
      
      if (response.code === '00000') {
        console.log('用户信息:', response.data);
        setUser(response.data);
      }
    } catch (error) {
      console.error('获取用户信息失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleGetMapData = async () => {
    try {
      setLoading(true);
      const response = await knowledgeMapApi.getMapData('1001');
      
      if (response.code === '00000') {
        console.log('知识地图数据:', response.data);
      }
    } catch (error) {
      console.error('获取知识地图数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      setLoading(true);
      const response = await userApi.logout();
      
      if (response.code === '00000') {
        console.log('登出成功');
        setUser(null);
      }
    } catch (error) {
      console.error('登出失败:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>API 使用示例</h2>
      
      <div style={{ marginBottom: '20px' }}>
        <button onClick={handleLogin} disabled={loading}>
          {loading ? '登录中...' : '登录'}
        </button>
        <button onClick={handleGetUserInfo} disabled={loading} style={{ marginLeft: '10px' }}>
          {loading ? '获取中...' : '获取用户信息'}
        </button>
        <button onClick={handleGetMapData} disabled={loading} style={{ marginLeft: '10px' }}>
          {loading ? '获取中...' : '获取知识地图数据'}
        </button>
        <button onClick={handleLogout} disabled={loading} style={{ marginLeft: '10px' }}>
          {loading ? '登出中...' : '登出'}
        </button>
      </div>

      {user && (
        <div style={{ marginTop: '20px', padding: '10px', border: '1px solid #ccc', borderRadius: '5px' }}>
          <h3>用户信息:</h3>
          <pre>{JSON.stringify(user, null, 2)}</pre>
        </div>
      )}
    </div>
  );
};

export default ApiUsageExample;
