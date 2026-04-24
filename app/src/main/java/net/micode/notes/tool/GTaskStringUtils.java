/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.tool;

/**
 * 谷歌任务（Google Tasks）同步工具类，集中定义了与 Google Tasks 进行数据交互时
 * 使用的 JSON 字段名、操作类型常量以及 MIUI 便签自定义的元数据相关字符串。
 * 主要用于 GTaskSyncService 及相关的异步任务，实现笔记的云端同步功能。
 */
public class GTaskStringUtils {

    // ==================== 操作相关 JSON 字段 ====================
    /** 操作 ID */
    public final static String GTASK_JSON_ACTION_ID = "action_id";
    /** 操作列表 */
    public final static String GTASK_JSON_ACTION_LIST = "action_list";
    /** 操作类型 */
    public final static String GTASK_JSON_ACTION_TYPE = "action_type";
    /** 操作类型：创建 */
    public final static String GTASK_JSON_ACTION_TYPE_CREATE = "create";
    /** 操作类型：获取全部 */
    public final static String GTASK_JSON_ACTION_TYPE_GETALL = "get_all";
    /** 操作类型：移动 */
    public final static String GTASK_JSON_ACTION_TYPE_MOVE = "move";
    /** 操作类型：更新 */
    public final static String GTASK_JSON_ACTION_TYPE_UPDATE = "update";

    // ==================== 实体属性 JSON 字段 ====================
    /** 创建者 ID */
    public final static String GTASK_JSON_CREATOR_ID = "creator_id";
    /** 子实体 */
    public final static String GTASK_JSON_CHILD_ENTITY = "child_entity";
    /** 客户端版本 */
    public final static String GTASK_JSON_CLIENT_VERSION = "client_version";
    /** 是否已完成 */
    public final static String GTASK_JSON_COMPLETED = "completed";
    /** 当前列表 ID */
    public final static String GTASK_JSON_CURRENT_LIST_ID = "current_list_id";
    /** 默认列表 ID */
    public final static String GTASK_JSON_DEFAULT_LIST_ID = "default_list_id";
    /** 是否已删除 */
    public final static String GTASK_JSON_DELETED = "deleted";
    /** 目标列表 */
    public final static String GTASK_JSON_DEST_LIST = "dest_list";
    /** 目标父节点 */
    public final static String GTASK_JSON_DEST_PARENT = "dest_parent";
    /** 目标父节点类型 */
    public final static String GTASK_JSON_DEST_PARENT_TYPE = "dest_parent_type";
    /** 实体变更集 */
    public final static String GTASK_JSON_ENTITY_DELTA = "entity_delta";
    /** 实体类型 */
    public final static String GTASK_JSON_ENTITY_TYPE = "entity_type";
    /** 获取已删除项 */
    public final static String GTASK_JSON_GET_DELETED = "get_deleted";
    /** 实体 ID */
    public final static String GTASK_JSON_ID = "id";
    /** 索引/排序位置 */
    public final static String GTASK_JSON_INDEX = "index";
    /** 最后修改时间 */
    public final static String GTASK_JSON_LAST_MODIFIED = "last_modified";
    /** 最新同步点 */
    public final static String GTASK_JSON_LATEST_SYNC_POINT = "latest_sync_point";
    /** 列表 ID */
    public final static String GTASK_JSON_LIST_ID = "list_id";
    /** 列表集合 */
    public final static String GTASK_JSON_LISTS = "lists";
    /** 名称/标题 */
    public final static String GTASK_JSON_NAME = "name";
    /** 新 ID（移动后新的 ID） */
    public final static String GTASK_JSON_NEW_ID = "new_id";
    /** 备注/笔记内容 */
    public final static String GTASK_JSON_NOTES = "notes";
    /** 父节点 ID */
    public final static String GTASK_JSON_PARENT_ID = "parent_id";
    /** 前一个兄弟节点 ID */
    public final static String GTASK_JSON_PRIOR_SIBLING_ID = "prior_sibling_id";
    /** 返回结果集 */
    public final static String GTASK_JSON_RESULTS = "results";
    /** 源列表 */
    public final static String GTASK_JSON_SOURCE_LIST = "source_list";
    /** 任务列表 */
    public final static String GTASK_JSON_TASKS = "tasks";
    /** 类型 */
    public final static String GTASK_JSON_TYPE = "type";
    /** 类型常量：分组 */
    public final static String GTASK_JSON_TYPE_GROUP = "GROUP";
    /** 类型常量：任务 */
    public final static String GTASK_JSON_TYPE_TASK = "TASK";
    /** 用户 */
    public final static String GTASK_JSON_USER = "user";

    // ==================== MIUI 便签自定义 ====================
    /** MIUI 便签在 Google Tasks 中的文件夹前缀，用于识别 MIUI 创建的文件夹 */
    public final static String MIUI_FOLDER_PREFFIX = "[MIUI_Notes]";
    /** 默认文件夹名 */
    public final static String FOLDER_DEFAULT = "Default";
    /** 通话记录文件夹名 */
    public final static String FOLDER_CALL_NOTE = "Call_Note";
    /** 元数据文件夹名（存储同步相关元数据） */
    public final static String FOLDER_META = "METADATA";

    // ==================== 元数据头部标识 ====================
    /** 元数据中的 gTask ID 键 */
    public final static String META_HEAD_GTASK_ID = "meta_gid";
    /** 元数据中的笔记信息键 */
    public final static String META_HEAD_NOTE = "meta_note";
    /** 元数据中的数据信息键 */
    public final static String META_HEAD_DATA = "meta_data";
    /** 元数据笔记的名称（用于标记元数据任务，提醒用户不要修改或删除） */
    public final static String META_NOTE_NAME = "[META INFO] DON'T UPDATE AND DELETE";
}