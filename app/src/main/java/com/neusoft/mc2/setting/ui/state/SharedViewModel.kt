/*
 * Copyright 2018-present KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.neusoft.mc2.setting.ui.state

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * TODO tip：callback-ViewModel 的职责仅限于 页面通信，不建议在此处理 UI 逻辑，
 * UI 逻辑只适合在 Activity/Fragment 等视图控制器中完成，是 “数据驱动” 的一部分，
 * 将来升级到 Jetpack Compose 更是如此。
 * <p>
 * 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/6257931840
 * <p>
 * Create by KunMinX at 19/10/16
 */

//一级列表
val CONNECT_LIST_TAG = "Connect"
val AUDIO_LIST_TAG = "Audio"
val DISPLAY_LIST_TAG = "Display"
val SYSTEM_LIST_TAG = "System"

class SharedViewModel : ViewModel() {

    val currentShowRightFragment = MutableLiveData<String>()

    val currentConnectFragment = MutableLiveData<String>()

    val currentSystemFragment = MutableLiveData<String>()

}