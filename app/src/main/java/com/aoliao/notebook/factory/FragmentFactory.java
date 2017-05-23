/*
 * Copyright 2016 XuJiaji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aoliao.notebook.factory;

import com.aoliao.notebook.fragment.BaseMainFragment;
import com.aoliao.notebook.ui.LoginActivity;
import com.aoliao.notebook.fragment.MainFragment;
import com.aoliao.notebook.helper.Config;
import com.aoliao.notebook.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by jiana on 16-11-6.
 */

public class FragmentFactory {
    /**
     * 主页Fragment数量
     */
    private static final int FRAGMENT_NUM = 11;

    /**
     * 统一管理MainActivity中所有Fragment
     */
    public static final Map<String, WeakReference<BaseMainFragment>> MAIN_WIND_FRAG = new HashMap<>(FRAGMENT_NUM);

    /**
     * 在主页展示的Fragment工厂
     * @param key
     * @return
     */
    public static BaseMainFragment newFragment(String key) {
        BaseMainFragment newFragment = null;
        switch (key) {
            case Config.fragment.HOME:
                newFragment = MainFragment.newInstance();
                break;

            default:
                break;
        }
        MAIN_WIND_FRAG.put(key, new WeakReference<BaseMainFragment>(newFragment));
        return newFragment;
    }

    /**
     * 通过首页菜单下标获取对应Fragment的名字
     * @param index
     * @return
     */
    public static String menuName(int index) {
        switch (index) {
            case 0:
                return Config.fragment.HOME;

            default:
                return "";
        }
    }

    /**
     * 获取MAIN_WIND_FRAG已经存在的Fragment
     * @param key
     * @return
     */
    public static BaseMainFragment getFrag(String key) {
        return getOrCreate(key);
    }

    /**
     * 获取MAIN_WIND_FRAG中对应的Fragment，如果没有则创建
     * @param key
     * @return
     */
    public static BaseMainFragment getOrCreate(String key) {
        WeakReference<BaseMainFragment> wr = MAIN_WIND_FRAG.get(key);
        if (wr == null || wr.get() == null) {
            LogUtil.e3("通过" + key + "没有获取到对应Fragment，将会创建一个新的");
            newFragment(key);
        }
        return MAIN_WIND_FRAG.get(key).get();
    }

    /**
     * 将MAIN_WIND_FRAG对应key的value删除
     * @param key
     */
    public static void rmFrag(String key) {
        MAIN_WIND_FRAG.remove(key);
        LogUtil.e3("remove key = " + key + "; value = " + MAIN_WIND_FRAG.get(key));
    }

    /**
     * 更新头像
     */
    public static void updatedUser() {
        for (String key : MAIN_WIND_FRAG.keySet()) {
            WeakReference<BaseMainFragment> wr = MAIN_WIND_FRAG.get(key);
            if (wr == null || wr.get() == null) continue;
            wr.get().setUpdatedUser(true);
        }
    }
}
