package iqq.app.ui.event;
 /*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import iqq.api.common.EventBase;

import java.io.Serializable;
import java.util.Map;

/**
 * Project  : iqq
 * Author   : solosky < solosky772@qq.com >
 * Created  : 5/3/14
 * License  : Apache License 2.0
 */
public class UIEvent extends EventBase implements Serializable {
    /**
     * 事件ID，定义在IMEventID
     */
    private UIEventType type;
    /**
     * 事件数据MAP，也可以直接把事件数据以KEY的形式放入MAP中，处理事件时按KEY读取出来，编程时要确保KEY一致
     */
    private Map<String, Object> data;

    public UIEvent(UIEventType type) {
        this.type = type;
    }

    public UIEvent() {
    }

    public UIEvent(UIEventType type, Object target) {
        this.type = type;
        this.target = target;
    }

    public UIEvent(UIEventType type, Object target, Map<String, Object> data) {
        this.type = type;
        this.target = target;
        this.data = data;
    }

    public UIEventType getType() {
        return type;
    }

    public void setType(UIEventType type) {
        this.type = type;
    }

    public void putData(String key, Object value) {
        this.data.put(key, value);
    }

    public boolean hasData(String key) {
        return this.data.containsKey(key);
    }

    public <T> T getData(String key) {
        return (T) this.data.get(key);
    }

    public Map<String, Object> getDataExt() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
