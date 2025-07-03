# 项目目录结构说明

## 📁 目录组织

```
src/
├── api/                    # API相关
│   ├── index.ts           # API工具函数和token管理
│   └── mockApi.ts         # Mock API数据
├── assets/                # 静态资源
│   └── react.svg          # 图片、图标等
├── components/            # 可复用组件
│   ├── Layout/            # 布局组件
│   ├── KnowledgeMap/      # 知识地图组件
│   ├── KnowledgeTree/     # 知识树组件
│   └── KnowledgeGlobe/    # 知识球组件
├── pages/                 # 路由页面组件
│   ├── IndexRoute/        # 首页路由判断
│   ├── Login/             # 登录页面
│   ├── Guide/             # 引导页面
│   ├── KnowledgeOrganize/ # 知识整理页面
│   ├── Review/            # 复习页面
│   ├── Community/         # 社区页面
│   └── Profile/           # 个人中心页面
├── utils/                 # 工具函数
│   ├── README.md          # 工具函数说明
│   └── request.ts         # HTTP请求工具
├── mock/                  # Mock数据
│   ├── mock-login.ts      # 登录Mock数据
│   └── mock-KnowledgeTree-get.ts # 知识树Mock数据
├── examples/              # 示例代码
│   └── ApiUsageExample.tsx # API使用示例
├── App.tsx                # 主应用组件
├── App.css                # 全局样式
├── main.tsx               # 应用入口
└── index.css              # 基础样式
```

## 🎯 目录分类原则

### 📄 Pages (页面组件)
- **定义**: 直接对应路由的页面级组件
- **特点**: 
  - 通常是完整的页面视图
  - 包含页面特定的业务逻辑
  - 可能包含多个子组件
  - 有独立的CSS样式文件

### 🧩 Components (可复用组件)
- **定义**: 可在多个页面中复用的UI组件
- **特点**:
  - 功能相对独立
  - 可配置性强
  - 不依赖特定的业务逻辑
  - 可在不同页面中重复使用

## 🚀 路由结构

```
/                          # 首页 - 判断首次访问
├── /login                 # 登录页面
├── /guide                 # 引导页面 (首次访问)
└── /app                   # 主应用 (带底部导航)
    ├── /knowledge-map     # 知识地图
    ├── /review            # 复习页面
    ├── /community         # 社区页面
    └── /profile           # 个人中心
```

## 📋 组件导入规范

```typescript
// 导入页面组件
import Login from './pages/Login';
import Guide from './pages/Guide';

// 导入可复用组件
import Layout from './components/Layout';
import KnowledgeMap from './components/KnowledgeMap';

// 导入工具函数
import { tokenUtils } from './api';
import { request } from './utils/request';
```

## 🔄 用户访问流程

1. **首次访问** (`/`) → 检查登录状态 → 跳转到引导页 (`/guide`)
2. **引导完成** → 跳转到知识地图 (`/app/knowledge-map`)
3. **后续访问** (`/`) → 检查登录状态 → 直接跳转到知识地图
4. **主应用导航** → 通过底部导航栏在各功能页面间切换

## 🎨 样式组织

- **全局样式**: `App.css`, `index.css`
- **组件样式**: 每个组件/页面都有对应的 `index.css` 文件
- **样式命名**: 使用BEM命名规范，避免样式冲突

这种目录结构清晰地分离了页面组件和可复用组件，便于项目的维护和扩展。
