import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';

// 根据环境配置baseURL
const getBaseURL = () => {
  // 开发环境使用相对路径，通过Vite代理转发
  if (import.meta.env.DEV) {
    return '';
  }
  // 生产环境使用绝对路径
  return import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
};

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: getBaseURL(),
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');

    // 如果token存在，添加到请求头的Authorization字段
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // 打印请求信息（开发环境）
    if (import.meta.env.DEV) {
      console.log('🚀 Request:', {
        url: config.url,
        method: config.method,
        headers: config.headers,
        data: config.data,
      });
    }

    return config;
  },
  (error) => {
    console.error('❌ Request Error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    // 打印响应信息（开发环境）
    if (import.meta.env.DEV) {
      console.log('✅ Response:', {
        url: response.config.url,
        status: response.status,
        data: response.data,
      });
    }

    // 统一处理响应数据
    const { data } = response;

    // 根据接口文档，后端返回的数据结构有code字段
    if (data && typeof data === 'object' && 'code' in data) {
      // 知识树接口使用数字类型的code，并且有success字段
      if (typeof data.code === 'number') {
        if (data.code === 200 && data.success === true) {
          return data; // 知识树接口成功时返回完整数据
        } else {
          // 知识树接口的业务错误
          return Promise.reject(new Error(data.message || '请求失败'));
        }
      }
      // 其他接口使用字符串类型的code
      else if (typeof data.code === 'string') {
        if (data.code === '00000') {
          return data; // 成功时返回完整数据
        } else if (data.code === 'A0200') {
          // 用户登录错误，清除本地token并跳转到登录页
          localStorage.removeItem('token');
          window.location.href = '/login';
          return Promise.reject(new Error(data.errorMessage || '登录已过期，请重新登录'));
        } else {
          // 其他业务错误
          return Promise.reject(new Error(data.errorMessage || data.userHint || '请求失败'));
        }
      }
    }

    return response.data;
  },
  (error) => {
    console.error('❌ Response Error:', error);

    // 处理HTTP状态码错误
    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 401:
          // 未授权，清除token并跳转登录
          localStorage.removeItem('token');
          window.location.href = '/login';
          break;
        case 403:
          console.error('权限不足');
          break;
        case 404:
          console.error('请求的资源不存在');
          break;
        case 500:
          console.error('服务器内部错误');
          break;
        default:
          console.error(`请求失败，状态码: ${status}`);
      }

      return Promise.reject(new Error(data?.errorMessage || data?.message || `请求失败，状态码: ${status}`));
    } else if (error.request) {
      // 网络错误
      console.error('网络错误，请检查网络连接');
      return Promise.reject(new Error('网络错误，请检查网络连接'));
    } else {
      // 其他错误
      console.error('请求配置错误:', error.message);
      return Promise.reject(error);
    }
  }
);

// 导出axios实例
export default request;

// 导出一些常用的请求方法（带泛型支持）
export const get = <T = any>(url: string, config?: InternalAxiosRequestConfig): Promise<T> => {
  return request.get(url, config);
};

export const post = <T = any>(url: string, data?: any, config?: InternalAxiosRequestConfig): Promise<T> => {
  return request.post(url, data, config);
};

export const put = <T = any>(url: string, data?: any, config?: InternalAxiosRequestConfig): Promise<T> => {
  return request.put(url, data, config);
};

export const del = <T = any>(url: string, config?: InternalAxiosRequestConfig): Promise<T> => {
  return request.delete(url, config);
};

// 文件上传方法
export const upload = <T = any>(url: string, formData: FormData, config?: InternalAxiosRequestConfig): Promise<T> => {
  return request.post(url, formData, {
    ...config,
    headers: {
      ...config?.headers,
      'Content-Type': 'multipart/form-data',
    },
  });
};

// Token管理工具函数
export const tokenUtils = {
  // 设置token
  setToken: (token: string) => {
    localStorage.setItem('token', token);
  },

  // 获取token
  getToken: () => {
    return localStorage.getItem('token');
  },

  // 移除token
  removeToken: () => {
    localStorage.removeItem('token');
  },

  // 检查token是否存在
  hasToken: () => {
    return !!localStorage.getItem('token');
  },
};
