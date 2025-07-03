import React, { useState } from "react";
import SearchBar from "./SearchBar";
import TreeVisualization from "./TreeVisualization";
import ActionButtons from "./ActionButtons";
import DocumentModal from "./DocumentModal";
import NodeDetails from "./NodeDetails";
import { useKnowledgeTree } from "./useKnowledgeTree";
import "./index.css";

const KnowledgeTree: React.FC = () => {
    const {
        // 状态
        searchTerm,
        treeData,
        loading,
        showDocumentList,
        documentsLoading,
        apiResponse,
        associating,
        currentTreeId,
        treeListLoading,

        // 方法
        handleSearch,
        handleHomeClick,
        handleDocumentButtonClick,
        handleDocumentSelect,
        showNodeDetails,
        closeNodeDetails,
        handleDetailsClick,
        setShowDocumentList,
        loadTreeData,
        setTreeData,
        getTreeDetailByTreeIdAndNodeId,
        setCurrentNodeId
    } = useKnowledgeTree();

    const [isShowHomeClick, setShowHomeClick] = useState(false);

    // 处理容器点击事件
    const handleContainerClick = (event: React.MouseEvent) => {
        const details = document.querySelector(".node-details");
        const documentList = document.querySelector(".document-modal");
        const target = event.target as HTMLElement;

        if (
            details?.classList.contains("show") &&
            !details.contains(target) &&
            !target.closest(".knowledge-tree-container")
        ) {
            closeNodeDetails();
        }

        // 点击其他地方关闭文档列表
        if (
            showDocumentList &&
            !documentList?.contains(target) &&
            !target.closest(".document-button")
        ) {
            setShowDocumentList(false);
        }
    };

    return (
        <div className="container" onClick={handleContainerClick}>
            {/* 搜索栏 */}
            {/* <SearchBar
                searchTerm={searchTerm}
                onSearch={handleSearch}
                disabled={loading || treeListLoading}
            /> */}

            {/* 树形图可视化 */}
            <TreeVisualization
                loading={loading}
                treeListLoading={treeListLoading}
                currentTreeId={currentTreeId}
                treeData={treeData}
                searchTerm={searchTerm}
                onNodeClick={showNodeDetails}
                setCurrentNodeId={setCurrentNodeId}
                loadTreeData={loadTreeData}
            />

            {/* 操作按钮 */}
            <ActionButtons
                onDocumentClick={handleDocumentButtonClick}
                isShowHomeClick={isShowHomeClick}
                onHomeClick={loadTreeData}
                documentsLoading={documentsLoading}
                currentTreeId={currentTreeId}
                loading={loading}
                treeListLoading={treeListLoading}
            />

            {/* 文档模态框 */}
            <DocumentModal
                show={showDocumentList}
                onClose={() => setShowDocumentList(false)}
                documentsLoading={documentsLoading}
                apiResponse={apiResponse}
                associating={associating}
                onDocumentSelect={handleDocumentSelect}
            />

            {/* 关联进度提示 */}
            {/* {associating && (
                <div className="associating-overlay">
                    <div className="associating-modal">
                        <div className="spinner"></div>
                        <div>正在关联文档到知识树...</div>
                        <div
                            style={{
                                fontSize: "12px",
                                color: "#666",
                                marginTop: "8px",
                            }}
                        >
                            AI正在分析文档内容并更新知识树结构
                        </div>
                    </div>
                </div>
            )} */}

            {/* 节点详情面板 */}
            <NodeDetails
                onClose={closeNodeDetails}
                onDetailsClick={ async() => {
                    setShowHomeClick(true);
                    const res = await getTreeDetailByTreeIdAndNodeId();
                    setTreeData(res.data.tree);
                }}
            />
        </div>
    );
};

export default KnowledgeTree;
