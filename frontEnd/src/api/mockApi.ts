// 模拟API服务
import { ApiResponse } from './index';

// 模拟延迟
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// 模拟用户数据
const mockUsers = [
  {
    id: 1,
    username: 'demo',
    email: 'demo@example.com',
    avatar: null
  },
  {
    id: 2,
    username: 'admin',
    email: 'admin@example.com',
    avatar: null
  }
];

// 模拟登录API
export const mockLogin = async (data: { username: string; password: string }): Promise<ApiResponse<{ token: string; userInfo: any }>> => {
  await delay(1000); // 模拟网络延迟
  
  // 简单验证 - 任何用户名和密码都可以登录
  if (data.username && data.password) {
    const user = mockUsers.find(u => u.username === data.username) || mockUsers[0];
    
    return {
      code: '00000',
      userHint: 'SUCCESS',
      data: {
        token: `mock-token-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
        userInfo: user
      }
    };
  } else {
    throw new Error('用户名和密码不能为空');
  }
};

// 模拟获取用户信息API
export const mockGetUserInfo = async (): Promise<ApiResponse<any>> => {
  await delay(500);
  
  return {
    code: '00000',
    userHint: 'SUCCESS',
    data: {
      id: 1,
      username: 'demo',
      email: 'demo@example.com',
      avatar: null,
      createdAt: '2024-01-01T00:00:00Z'
    }
  };
};

// 模拟登出API
export const mockLogout = async (): Promise<ApiResponse<any>> => {
  await delay(300);
  
  return {
    code: '00000',
    userHint: 'SUCCESS',
    data: null
  };
};
