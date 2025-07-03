// MongoDB初始化脚本 - 无验证版本
// 运行方法: mongosh mongodb://localhost:27017/enjoy_learning mongodb_init.js

// 使用数据库
db = db.getSiblingDB('enjoy_learning');

// 清空已有集合（如果需要重新创建）
db.learning_notes.drop();
db.wisdom_records.drop();
db.knowledge_trees.drop();
db.knowledge_nodes.drop();
db.note_knowledge_associations.drop();

// 创建集合（无验证规则）
db.createCollection("learning_notes");
db.createCollection("wisdom_records");
db.createCollection("knowledge_trees");
db.createCollection("knowledge_nodes");
db.createCollection("note_knowledge_associations");

// 创建索引
// learning_notes 集合的索引
db.learning_notes.createIndex({"userId": 1, "status": 1, "createdAt": -1});
db.learning_notes.createIndex({"tags": 1});
db.learning_notes.createIndex({"aiAnalysis.status": 1});
db.learning_notes.createIndex({"aiAnalysis.subjectArea": 1});
db.learning_notes.createIndex({"aiAnalysis.extractedKeywords": 1});
db.learning_notes.createIndex({"relatedKnowledgePoints": 1});

// wisdom_records 集合的索引
db.wisdom_records.createIndex({"userId": 1, "createdAt": -1});
db.wisdom_records.createIndex({"noteId": 1}, {"unique": true});
db.wisdom_records.createIndex({"knowledgePointId": 1});
db.wisdom_records.createIndex({"baseWisdomValue": -1});
db.wisdom_records.createIndex({"importanceLevel": 1});
db.wisdom_records.createIndex({"difficultyLevel": 1});

// knowledge_trees 集合的索引
db.knowledge_trees.createIndex({"userId": 1});
db.knowledge_trees.createIndex({"treeId": 1}, {"unique": true});

// knowledge_nodes 集合的索引
db.knowledge_nodes.createIndex({"treeId": 1, "userId": 1});
db.knowledge_nodes.createIndex({"nodeId": 1}, {"unique": true});
db.knowledge_nodes.createIndex({"parentNodeId": 1});
db.knowledge_nodes.createIndex({"keywords": 1});
db.knowledge_nodes.createIndex({"associatedNotes.noteId": 1});

// note_knowledge_associations 集合的索引
db.note_knowledge_associations.createIndex({"userId": 1, "noteId": 1});
db.note_knowledge_associations.createIndex({"treeId": 1, "nodeId": 1});
db.note_knowledge_associations.createIndex({"knowledgePointId": 1});

print("MongoDB 集合和索引初始化完成（无验证规则）!");
