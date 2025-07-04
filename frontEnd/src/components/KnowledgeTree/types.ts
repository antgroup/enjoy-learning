export interface TreeNodeData {
  name: string;
  value?: number;
  children?: TreeNodeData[];
  nodeId?: string;
  level?: number;
  progress?: number;
  description?: string;
  keywords?: string[];
  associatedNotesCount?: number;
  isAutoGenerated?: boolean;
  status?: string; // 添加状态字段
  lineStyle?: {    // 添加线条样式字段
    type?: string;
    color?: string;
    width?: number;
    opacity?: number;
  };
  itemStyle?: {
    color?: string;
    borderColor?: string;
    borderWidth?: number;
    shadowBlur?: number;
    shadowColor?: string;
    shadowOffsetY?: number;
    opacity?: number;
  };
  label?: {
    show?: boolean;
    position?: string;
    fontSize?: number;
    fontWeight?: string;
    rotate?: number;
    verticalAlign?: string;
    align?: string;
  };
}

export interface KnowledgeTreeProps {
  // 可以根据需要添加props
}

export interface SearchBarProps {
  searchTerm: string;
  onSearch: (term: string) => void;
  disabled?: boolean;
}

export interface TreeVisualizationProps {
  loading: boolean;
  treeListLoading: boolean;
  currentTreeId: number | null;
  treeData: TreeNodeData | null;
  searchTerm: string;
  onNodeClick: (nodeData: any) => void;
  setCurrentNodeId: (nodeId: string) => void;
  loadTreeData: () => void;
}

export interface DocumentModalProps {
  show: boolean;
  onClose: () => void;
  documentsLoading: boolean;
  apiResponse: any;
  associating: boolean;
  onDocumentSelect: (document: any) => void;
  handleClickDetails?: () => void;
}

export interface NodeDetailsProps {
  onClose: () => void;
  onDetailsClick?: () => void;
}

export interface ActionButtonsProps {
  onDocumentClick: () => void;
  onHomeClick: () => void;
  documentsLoading: boolean;
  currentTreeId: number | null;
  loading: boolean;
  treeListLoading: boolean;
  isShowHomeClick: boolean;
}
