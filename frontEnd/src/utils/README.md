# Axios 请求配置说明

这个目录包含了项目的 HTTP 请求配置，包括 axios 实例初始化、请求/响应拦截器以及 token 管理。

## 文件结构

```
src/
├── utils/
│   ├── request.ts          # axios 实例配置
│   └── README.md          # 使用说明
├── api/
│   └── index.ts           # API 接口定义
└── examples/
    └── ApiUsageExample.tsx # 使用示例
```

## 核心功能

### 1. 自动 Token 管理

请求拦截器会自动从 `localStorage` 中获取 token 并添加到请求头：

```typescript
// 自动添加 Authorization header
config.headers.Authorization = `Bearer ${token}`;
```

### 2. 统一错误处理

响应拦截器统一处理各种错误情况：

- **401 未授权**: 自动清除 token 并跳转登录页
- **403 权限不足**: 显示权限错误
- **404 资源不存在**: 显示资源不存在错误
- **500 服务器错误**: 显示服务器错误
- **网络错误**: 显示网络连接错误

### 3. 开发环境日志

在开发环境下自动打印请求和响应信息，方便调试：

```typescript
// 请求日志
console.log('🚀 Request:', {
  url: config.url,
  method: config.method,
  headers: config.headers,
  data: config.data,
});

// 响应日志
console.log('✅ Response:', {
  url: response.config.url,
  status: response.status,
  data: response.data,
});
```

## 使用方法

### 1. 基础使用

```typescript
import request from '../utils/request';

// 直接使用 axios 实例
const response = await request.get('/api/users');
const result = await request.post('/api/users', userData);
```

### 2. 使用封装的方法

```typescript
import { get, post, put, del } from '../utils/request';

// 使用封装的方法（支持泛型）
const users = await get<User[]>('/users');
const user = await post<User>('/users', userData);
const updated = await put<User>(`/users/${id}`, updateData);
await del(`/users/${id}`);
```

### 3. 使用 API 模块

```typescript
import { userApi, tokenUtils } from '../api';

// 登录并保存 token
const response = await userApi.login({ username, password });
tokenUtils.setToken(response.data.token);

// 获取用户信息（自动携带 token）
const userInfo = await userApi.getUserInfo();
```

### 4. Token 管理

```typescript
import { tokenUtils } from '../api';

// 设置 token
tokenUtils.setToken('your-jwt-token');

// 获取 token
const token = tokenUtils.getToken();

// 检查是否有 token
if (tokenUtils.hasToken()) {
  // 用户已登录
}

// 清除 token
tokenUtils.removeToken();
```

### 5. 文件上传

```typescript
import { upload } from '../utils/request';

const formData = new FormData();
formData.append('file', file);

const response = await upload<{ url: string }>('/upload', formData);
```

## 配置选项

### 环境变量

可以通过环境变量配置 API 基础 URL：

```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8080/api

# .env.production
VITE_API_BASE_URL=https://api.yourapp.com
```

### 超时设置

默认请求超时时间为 10 秒，可以在 `request.ts` 中修改：

```typescript
const request = axios.create({
  timeout: 10000, // 10 秒超时
  // ...
});
```

### 请求头设置

默认请求头为 `application/json`，文件上传时会自动设置为 `multipart/form-data`。

## API 响应格式

项目假设后端 API 返回以下格式：

```typescript
interface ApiResponse<T> {
  code: number;    // 状态码，200 表示成功
  message: string; // 响应消息
  data: T;        // 实际数据
}
```

如果你的后端 API 格式不同，可以在 `request.ts` 的响应拦截器中修改处理逻辑。

## 错误处理

### 业务错误

```typescript
try {
  const response = await userApi.login(credentials);
  // 处理成功响应
} catch (error) {
  // 处理错误
  console.error('登录失败:', error.message);
}
```

### 全局错误处理

响应拦截器会自动处理以下情况：

1. **Token 过期**: 自动清除本地 token 并跳转登录页
2. **网络错误**: 显示网络连接错误提示
3. **服务器错误**: 显示相应的错误信息

## 最佳实践

### 1. 统一 API 管理

将所有 API 接口定义在 `src/api/` 目录下，按模块分类：

```typescript
// src/api/user.ts
export const userApi = {
  login: (data) => post('/auth/login', data),
  getUserInfo: () => get('/user/info'),
  // ...
};

// src/api/knowledge.ts
export const knowledgeApi = {
  getMapData: (id) => get(`/knowledge-map/${id}`),
  // ...
};
```

### 2. 类型安全

使用 TypeScript 泛型确保类型安全：

```typescript
interface User {
  id: number;
  username: string;
  email: string;
}

const user = await get<ApiResponse<User>>('/user/info');
// user.data 的类型为 User
```

### 3. 错误边界

在组件中使用 try-catch 处理 API 错误：

```typescript
const [loading, setLoading] = useState(false);
const [error, setError] = useState('');

const fetchData = async () => {
  try {
    setLoading(true);
    setError('');
    const data = await api.getData();
    // 处理数据
  } catch (err) {
    setError(err.message);
  } finally {
    setLoading(false);
  }
};
```

## 示例

查看 `src/examples/ApiUsageExample.tsx` 文件获取完整的使用示例。

## 注意事项

1. **Token 存储**: 当前使用 `localStorage` 存储 token，在生产环境中考虑安全性
2. **HTTPS**: 生产环境务必使用 HTTPS 协议
3. **CORS**: 确保后端正确配置 CORS 策略
4. **错误处理**: 根据实际业务需求调整错误处理逻辑
