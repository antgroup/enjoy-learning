// Mock数据，模拟接口文档中的响应格式

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface TreeNode {
  name: string;
  category: string;
  progress?: number;
  description?: string;
  relatedNodes?: string[];
  children?: TreeNode[];
}

export interface KnowledgeTreeData {
  name: string;
  category: string;
  children: TreeNode[];
}

// 模拟获取知识树数据的API响应
export const mockKnowledgeTreeData: ApiResponse<KnowledgeTreeData> = {
  code: 200,
  message: "success",
  data: {
    name: "我的知识体系",
    category: "核心",
    children: [
      {
        name: "前端开发",
        category: "技术",
        progress: 80,
        description: "前端开发是创建网站或应用的Web界面呈现给用户的过程。",
        children: [
          {
            name: "HTML5",
            category: "前端开发",
            progress: 90,
            description: "HTML5是HTML的最新版本，提供了更多的语义化标签和API。",
            relatedNodes: ["CSS3", "JavaScript", "Web API"]
          },
          {
            name: "CSS3",
            category: "前端开发",
            progress: 85,
            description: "CSS3是层叠样式表的最新版本，支持动画、渐变等新特性。",
            relatedNodes: ["HTML5", "Sass", "响应式设计"]
          },
          {
            name: "JavaScript",
            category: "前端开发",
            progress: 75,
            description: "JavaScript是一种动态的、弱类型的编程语言。",
            relatedNodes: ["TypeScript", "Node.js", "React"],
            children: [
              {
                name: "ES6+",
                category: "前端开发",
                progress: 70,
                description: "ECMAScript 6及更新版本的JavaScript特性。",
                relatedNodes: ["箭头函数", "Promise", "模块化"]
              },
              {
                name: "DOM操作",
                category: "前端开发",
                progress: 80,
                description: "文档对象模型的操作和事件处理。",
                relatedNodes: ["事件处理", "选择器", "动态内容"]
              },
              {
                name: "异步编程",
                category: "前端开发",
                progress: 65,
                description: "JavaScript中的异步编程模式和技术。",
                relatedNodes: ["Promise", "async/await", "事件循环"]
              }
            ]
          },
          {
            name: "React",
            category: "前端框架",
            progress: 65,
            description: "用于构建用户界面的JavaScript库。",
            relatedNodes: ["JSX", "组件", "状态管理"],
            children: [
              {
                name: "React Hooks",
                category: "前端框架",
                progress: 60,
                description: "React的函数式组件状态管理方案。",
                relatedNodes: ["useState", "useEffect", "自定义Hook"]
              },
              {
                name: "React Router",
                category: "前端框架",
                progress: 55,
                description: "React应用的路由管理库。",
                relatedNodes: ["路由配置", "导航", "参数传递"]
              },
              {
                name: "状态管理",
                category: "前端框架",
                progress: 50,
                description: "React应用的状态管理解决方案。",
                relatedNodes: ["Redux", "Context API", "Zustand"]
              }
            ]
          },
          {
            name: "Vue.js",
            category: "前端框架",
            progress: 45,
            description: "渐进式JavaScript框架。",
            relatedNodes: ["组件化", "响应式", "指令"],
            children: [
              {
                name: "Vue 3",
                category: "前端框架",
                progress: 40,
                description: "Vue.js的最新版本，支持Composition API。",
                relatedNodes: ["Composition API", "Teleport", "Fragments"]
              }
            ]
          }
        ]
      },
      {
        name: "后端开发",
        category: "技术",
        progress: 60,
        description: "服务器端应用程序的开发。",
        children: [
          {
            name: "Node.js",
            category: "后端技术",
            progress: 70,
            description: "基于Chrome V8引擎的JavaScript运行时。",
            relatedNodes: ["Express", "npm", "异步编程"],
            children: [
              {
                name: "Express.js",
                category: "后端技术",
                progress: 65,
                description: "Node.js的Web应用框架。",
                relatedNodes: ["中间件", "路由", "模板引擎"]
              },
              {
                name: "Koa.js",
                category: "后端技术",
                progress: 40,
                description: "下一代Node.js Web框架。",
                relatedNodes: ["async/await", "洋葱模型", "中间件"]
              }
            ]
          },
          {
            name: "Python",
            category: "后端技术",
            progress: 65,
            description: "简洁而强大的编程语言。",
            relatedNodes: ["Django", "Flask", "数据分析"],
            children: [
              {
                name: "Django",
                category: "后端技术",
                progress: 50,
                description: "Python的高级Web框架。",
                relatedNodes: ["ORM", "模板", "中间件"]
              },
              {
                name: "Flask",
                category: "后端技术",
                progress: 45,
                description: "轻量级的Python Web框架。",
                relatedNodes: ["蓝图", "扩展", "模板"]
              }
            ]
          },
          {
            name: "Java",
            category: "后端技术",
            progress: 55,
            description: "企业级应用开发的主流语言。",
            relatedNodes: ["Spring", "Maven", "JVM"],
            children: [
              {
                name: "Spring Boot",
                category: "后端技术",
                progress: 50,
                description: "简化Spring应用开发的框架。",
                relatedNodes: ["自动配置", "起步依赖", "监控"]
              }
            ]
          },
          {
            name: "数据库",
            category: "后端技术",
            progress: 55,
            description: "数据存储和管理系统。",
            children: [
              {
                name: "MySQL",
                category: "后端技术",
                progress: 60,
                description: "关系型数据库管理系统。",
                relatedNodes: ["SQL", "索引", "事务"]
              },
              {
                name: "MongoDB",
                category: "后端技术",
                progress: 45,
                description: "面向文档的NoSQL数据库。",
                relatedNodes: ["文档存储", "聚合", "索引"]
              },
              {
                name: "Redis",
                category: "后端技术",
                progress: 50,
                description: "内存数据结构存储系统。",
                relatedNodes: ["缓存", "发布订阅", "数据类型"]
              }
            ]
          }
        ]
      },
      {
        name: "数据科学",
        category: "技术",
        progress: 40,
        description: "从数据中提取知识和洞察的跨学科领域。",
        children: [
          {
            name: "机器学习",
            category: "数据科学",
            progress: 35,
            description: "让计算机从数据中学习的算法和技术。",
            relatedNodes: ["监督学习", "无监督学习", "深度学习"],
            children: [
              {
                name: "深度学习",
                category: "数据科学",
                progress: 25,
                description: "基于神经网络的机器学习方法。",
                relatedNodes: ["神经网络", "TensorFlow", "PyTorch"]
              },
              {
                name: "自然语言处理",
                category: "数据科学",
                progress: 30,
                description: "计算机理解和处理人类语言的技术。",
                relatedNodes: ["文本分析", "语言模型", "情感分析"]
              }
            ]
          },
          {
            name: "数据可视化",
            category: "数据科学",
            progress: 50,
            description: "将数据转换为图形和图表的技术。",
            relatedNodes: ["D3.js", "ECharts", "Tableau"],
            children: [
              {
                name: "D3.js",
                category: "数据科学",
                progress: 45,
                description: "基于数据驱动的文档操作库。",
                relatedNodes: ["SVG", "数据绑定", "交互"]
              }
            ]
          },
          {
            name: "数据分析",
            category: "数据科学",
            progress: 40,
            description: "从数据中发现模式和洞察的过程。",
            relatedNodes: ["统计学", "Python", "R语言"],
            children: [
              {
                name: "统计学基础",
                category: "数据科学",
                progress: 35,
                description: "数据分析的数学基础。",
                relatedNodes: ["概率论", "假设检验", "回归分析"]
              }
            ]
          }
        ]
      },
      {
        name: "DevOps",
        category: "技术",
        progress: 30,
        description: "开发和运维的结合，提高软件交付效率。",
        children: [
          {
            name: "容器化",
            category: "DevOps",
            progress: 35,
            description: "使用容器技术部署和管理应用。",
            relatedNodes: ["Docker", "Kubernetes", "微服务"],
            children: [
              {
                name: "Docker",
                category: "DevOps",
                progress: 40,
                description: "轻量级容器化平台。",
                relatedNodes: ["镜像", "容器", "Dockerfile"]
              },
              {
                name: "Kubernetes",
                category: "DevOps",
                progress: 25,
                description: "容器编排和管理平台。",
                relatedNodes: ["Pod", "Service", "Deployment"]
              }
            ]
          },
          {
            name: "CI/CD",
            category: "DevOps",
            progress: 30,
            description: "持续集成和持续部署。",
            relatedNodes: ["Jenkins", "GitLab CI", "自动化测试"],
            children: [
              {
                name: "Jenkins",
                category: "DevOps",
                progress: 25,
                description: "开源的持续集成工具。",
                relatedNodes: ["Pipeline", "插件", "构建"]
              }
            ]
          }
        ]
      }
    ]
  }
};

// 模拟API调用函数
export const fetchKnowledgeTreeData = async (userId: number, treeId?: number): Promise<ApiResponse<KnowledgeTreeData>> => {
  // 模拟网络延迟
  await new Promise(resolve => setTimeout(resolve, 300));
  
  return mockKnowledgeTreeData;
};

// 模拟搜索API
export interface SearchResult {
  nodeId: string;
  name: string;
  category: string;
  description: string;
  progress: number;
  path: string[];
}

export const searchKnowledgeNodes = async (userId: number, keyword: string): Promise<ApiResponse<SearchResult[]>> => {
  // 模拟网络延迟
  await new Promise(resolve => setTimeout(resolve, 200));
  
  // 简单的搜索逻辑
  const results: SearchResult[] = [];
  const searchInTree = (node: TreeNode, path: string[] = []) => {
    const currentPath = [...path, node.name];
    
    if (node.name.toLowerCase().includes(keyword.toLowerCase())) {
      results.push({
        nodeId: `node_${results.length + 1}`,
        name: node.name,
        category: node.category,
        description: node.description || '',
        progress: node.progress || 0,
        path: currentPath
      });
    }
    
    if (node.children) {
      node.children.forEach(child => searchInTree(child, currentPath));
    }
  };
  
  mockKnowledgeTreeData.data.children.forEach(child => 
    searchInTree(child, [mockKnowledgeTreeData.data.name])
  );
  
  return {
    code: 200,
    message: "success",
    data: results
  };
};
