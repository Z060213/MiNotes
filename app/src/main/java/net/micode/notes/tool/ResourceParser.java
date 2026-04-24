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

import android.content.Context;
import android.preference.PreferenceManager;

import net.micode.notes.R;
import net.micode.notes.ui.NotesPreferenceActivity;

/**
 * 资源解析工具类，负责将应用中定义的背景颜色、文字大小等逻辑常量
 * 映射为实际的 Android 资源 ID（布局、图片、样式等）。
 * 包含笔记编辑背景、列表项背景、桌面小部件背景以及文字外观等资源。
 */
public class ResourceParser {

    // ==================== 背景颜色常量 ====================
    /** 黄色 */
    public static final int YELLOW           = 0;
    /** 蓝色 */
    public static final int BLUE             = 1;
    /** 白色 */
    public static final int WHITE            = 2;
    /** 绿色 */
    public static final int GREEN            = 3;
    /** 红色 */
    public static final int RED              = 4;

    /** 默认背景颜色（黄色） */
    public static final int BG_DEFAULT_COLOR = YELLOW;

    // ==================== 文字大小常量 ====================
    /** 小号字体 */
    public static final int TEXT_SMALL       = 0;
    /** 中号字体 */
    public static final int TEXT_MEDIUM      = 1;
    /** 大号字体 */
    public static final int TEXT_LARGE       = 2;
    /** 超大号字体 */
    public static final int TEXT_SUPER       = 3;

    /** 默认字体大小（中号） */
    public static final int BG_DEFAULT_FONT_SIZE = TEXT_MEDIUM;

    /**
     * 笔记编辑界面的背景资源
     * 根据背景颜色索引获取对应的编辑区域背景图和标题栏背景图
     */
    public static class NoteBgResources {
        // 编辑区域背景图数组，按颜色常量顺序排列
        private final static int [] BG_EDIT_RESOURCES = new int [] {
                R.drawable.edit_yellow,
                R.drawable.edit_blue,
                R.drawable.edit_white,
                R.drawable.edit_green,
                R.drawable.edit_red
        };

        // 编辑界面标题栏背景图数组
        private final static int [] BG_EDIT_TITLE_RESOURCES = new int [] {
                R.drawable.edit_title_yellow,
                R.drawable.edit_title_blue,
                R.drawable.edit_title_white,
                R.drawable.edit_title_green,
                R.drawable.edit_title_red
        };

        /**
         * 获取编辑区域背景图资源 ID
         * @param id 背景颜色常量（YELLOW, BLUE, WHITE, GREEN, RED）
         * @return 编辑区域背景图资源 ID
         */
        public static int getNoteBgResource(int id) {
            return BG_EDIT_RESOURCES[id];
        }

        /**
         * 获取编辑界面标题栏背景图资源 ID
         * @param id 背景颜色常量
         * @return 标题栏背景图资源 ID
         */
        public static int getNoteTitleBgResource(int id) {
            return BG_EDIT_TITLE_RESOURCES[id];
        }
    }

    /**
     * 获取默认的背景颜色常量。
     * 如果用户在设置中开启了随机背景色，则随机返回一个颜色索引；
     * 否则返回默认颜色（黄色）。
     *
     * @param context 上下文
     * @return 背景颜色常量
     */
    public static int getDefaultBgId(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                NotesPreferenceActivity.PREFERENCE_SET_BG_COLOR_KEY, false)) {
            return (int) (Math.random() * NoteBgResources.BG_EDIT_RESOURCES.length);
        } else {
            return BG_DEFAULT_COLOR;
        }
    }

    /**
     * 笔记列表项的背景资源
     * 根据列表中笔记的位置（第一个、中间、最后一个、单个）提供不同的背景图，
     * 实现圆角列表效果。
     */
    public static class NoteItemBgResources {
        // 列表第一项的背景图
        private final static int [] BG_FIRST_RESOURCES = new int [] {
                R.drawable.list_yellow_up,
                R.drawable.list_blue_up,
                R.drawable.list_white_up,
                R.drawable.list_green_up,
                R.drawable.list_red_up
        };

        // 列表中间项的背景图
        private final static int [] BG_NORMAL_RESOURCES = new int [] {
                R.drawable.list_yellow_middle,
                R.drawable.list_blue_middle,
                R.drawable.list_white_middle,
                R.drawable.list_green_middle,
                R.drawable.list_red_middle
        };

        // 列表最后一项的背景图
        private final static int [] BG_LAST_RESOURCES = new int [] {
                R.drawable.list_yellow_down,
                R.drawable.list_blue_down,
                R.drawable.list_white_down,
                R.drawable.list_green_down,
                R.drawable.list_red_down,
        };

        // 列表中仅有一项时的背景图
        private final static int [] BG_SINGLE_RESOURCES = new int [] {
                R.drawable.list_yellow_single,
                R.drawable.list_blue_single,
                R.drawable.list_white_single,
                R.drawable.list_green_single,
                R.drawable.list_red_single
        };

        public static int getNoteBgFirstRes(int id) {
            return BG_FIRST_RESOURCES[id];
        }

        public static int getNoteBgLastRes(int id) {
            return BG_LAST_RESOURCES[id];
        }

        public static int getNoteBgSingleRes(int id) {
            return BG_SINGLE_RESOURCES[id];
        }

        public static int getNoteBgNormalRes(int id) {
            return BG_NORMAL_RESOURCES[id];
        }

        /** 获取文件夹列表项背景图（固定图） */
        public static int getFolderBgRes() {
            return R.drawable.list_folder;
        }
    }

    /**
     * 桌面小部件的背景资源
     * 包括 2x 和 4x 两种尺寸的小部件背景图
     */
    public static class WidgetBgResources {
        // 2x 小部件背景图数组
        private final static int [] BG_2X_RESOURCES = new int [] {
                R.drawable.widget_2x_yellow,
                R.drawable.widget_2x_blue,
                R.drawable.widget_2x_white,
                R.drawable.widget_2x_green,
                R.drawable.widget_2x_red,
        };

        public static int getWidget2xBgResource(int id) {
            return BG_2X_RESOURCES[id];
        }

        // 4x 小部件背景图数组
        private final static int [] BG_4X_RESOURCES = new int [] {
                R.drawable.widget_4x_yellow,
                R.drawable.widget_4x_blue,
                R.drawable.widget_4x_white,
                R.drawable.widget_4x_green,
                R.drawable.widget_4x_red
        };

        public static int getWidget4xBgResource(int id) {
            return BG_4X_RESOURCES[id];
        }
    }

    /**
     * 文字外观（字体大小）资源
     * 对应不同文字大小常量，返回对应的样式资源 ID
     */
    public static class TextAppearanceResources {
        // 文字外观样式数组，顺序：TEXT_SMALL(0), TEXT_MEDIUM(1), TEXT_LARGE(2), TEXT_SUPER(3)
        private final static int [] TEXTAPPEARANCE_RESOURCES = new int [] {
                R.style.TextAppearanceNormal,
                R.style.TextAppearanceMedium,
                R.style.TextAppearanceLarge,
                R.style.TextAppearanceSuper
        };

        /**
         * 根据文字大小常量获取对应的文本外观样式资源 ID。
         * 如果传入的 id 超出数组范围（如 SharedPreferences 中存储了错误的值），
         * 则返回默认字体大小对应的样式。
         */
        public static int getTexAppearanceResource(int id) {
            /**
             * HACKME: Fix bug of store the resource id in shared preference.
             * The id may larger than the length of resources, in this case,
             * return the {@link ResourceParser#BG_DEFAULT_FONT_SIZE}
             */
            if (id >= TEXTAPPEARANCE_RESOURCES.length) {
                return BG_DEFAULT_FONT_SIZE;
            }
            return TEXTAPPEARANCE_RESOURCES[id];
        }

        public static int getResourcesSize() {
            return TEXTAPPEARANCE_RESOURCES.length;
        }
    }
}