import React from "react";
import { ActionButtonsProps } from "./types";

const ActionButtons: React.FC<ActionButtonsProps> = ({
    onDocumentClick,
    onHomeClick,
    documentsLoading,
    currentTreeId,
    loading,
    treeListLoading,
    isShowHomeClick,
}) => {
    return (
        <div className="document-button-container">
            {/* Home按钮 */}
            {isShowHomeClick ? (
                <button
                    className="document-button home-button"
                    onClick={onHomeClick}
                    disabled={loading || treeListLoading}
                    title="重新初始化图表"
                    style={{ marginRight: "10px" }}
                >
                    <span className="document-icon">🏠</span>
                </button>
            ) : null}

            {/* 文档按钮 */}
            <button
                className="document-button"
                onClick={onDocumentClick}
                disabled={documentsLoading || !currentTreeId}
                title="查看所有文档"
            >
                <span className="document-icon">
                    {documentsLoading ? "⏳" : "+"}
                </span>
            </button>
        </div>
    );
};

export default ActionButtons;
