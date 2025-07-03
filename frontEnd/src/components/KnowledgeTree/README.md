# KnowledgeTree 组件拆分说明

## 概述
KnowledgeTree 组件已经被拆分成多个更小的、职责单一的子组件，提高了代码的可维护性和可复用性。

## 组件结构

```
KnowledgeTree/
├── index.tsx              # 主组件，组合所有子组件
├── types.ts               # TypeScript 类型定义
├── useKnowledgeTree.ts    # 自定义Hook，管理所有业务逻辑
├── SearchBar.tsx          # 搜索栏组件
├── TreeVisualization.tsx  # 树形图可视化组件
├── ActionButtons.tsx      # 操作按钮组件
├── DocumentModal.tsx      # 文档选择模态框组件
├── NodeDetails.tsx        # 节点详情面板组件
├── index.css              # 样式文件
└── README.md              # 说明文档
```

## 组件职责

### 1. `index.tsx` - 主组件
- **职责**: 组合所有子组件，处理全局事件
- **特点**: 
  - 使用自定义Hook管理状态和逻辑
  - 负责组件间的数据传递
  - 处理容器级别的点击事件

### 2. `types.ts` - 类型定义
- **职责**: 定义所有组件使用的TypeScript类型
- **包含**:
  - `TreeNodeData` - 树节点数据结构
  - 各个组件的Props接口
  - 通用类型定义

### 3. `useKnowledgeTree.ts` - 自定义Hook
- **职责**: 管理所有业务逻辑和状态
- **功能**:
  - 知识树数据加载和管理
  - 文档关联功能
  - 搜索功能
  - 节点详情显示
  - API调用和错误处理

### 4. `SearchBar.tsx` - 搜索栏组件
- **职责**: 提供搜索功能的UI
- **特点**:
  - 受控组件，接收搜索词和回调函数
  - 包含清除搜索功能
  - 支持禁用状态

### 5. `TreeVisualization.tsx` - 树形图可视化组件
- **职责**: 渲染ECharts树形图
- **功能**:
  - 初始化和管理ECharts实例
  - 处理图表交互事件
  - 搜索结果高亮显示
  - 响应式图表大小调整

### 6. `ActionButtons.tsx` - 操作按钮组件
- **职责**: 提供操作按钮UI
- **包含**:
  - Home按钮 - 重新初始化图表
  - 文档按钮 - 打开文档选择模态框

### 7. `DocumentModal.tsx` - 文档模态框组件
- **职责**: 显示文档列表并处理文档选择
- **功能**:
  - 文档列表展示
  - 文档关联操作
  - 加载状态和错误处理

### 8. `NodeDetails.tsx` - 节点详情面板组件
- **职责**: 显示选中节点的详细信息
- **功能**:
  - 节点基本信息展示
  - 学习进度显示
  - 关键词标签展示

## 设计原则

### 1. 单一职责原则
每个组件只负责一个特定的功能，便于理解和维护。

### 2. 组件复用性
子组件设计为可复用，可以在其他地方独立使用。

### 3. 状态管理集中化
使用自定义Hook集中管理所有状态和业务逻辑，避免状态分散。

### 4. 类型安全
使用TypeScript提供完整的类型定义，确保类型安全。

### 5. 关注点分离
将UI渲染、业务逻辑、类型定义分离到不同文件中。

## 使用方式

### 导入主组件
```typescript
import KnowledgeTree from './components/KnowledgeTree';

// 使用
<KnowledgeTree />
```

### 导入子组件（如需单独使用）
```typescript
import SearchBar from './components/KnowledgeTree/SearchBar';
import { TreeNodeData } from './components/KnowledgeTree/types';
```

### 使用自定义Hook
```typescript
import { useKnowledgeTree } from './components/KnowledgeTree/useKnowledgeTree';

const MyComponent = () => {
  const { treeData, loading, handleSearch } = useKnowledgeTree();
  // ...
};
```

## 优势

1. **可维护性**: 代码结构清晰，易于理解和修改
2. **可测试性**: 每个组件可以独立测试
3. **可复用性**: 子组件可以在其他地方复用
4. **性能优化**: 可以针对特定组件进行优化
5. **团队协作**: 不同开发者可以并行开发不同组件

## 扩展建议

1. **添加单元测试**: 为每个组件和Hook编写测试用例
2. **性能优化**: 使用React.memo优化不必要的重渲染
3. **错误边界**: 添加错误边界组件处理异常情况
4. **国际化**: 支持多语言
5. **主题定制**: 支持主题切换功能
