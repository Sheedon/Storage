package org.sheedon.storagelibrary;

/**
 * 插件类型SDK
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/7/10 3:45 PM
 */
public class PluginTypeModel<T> {

    public static final int TYPE_CALC_NUM = 1001;// 计算数量
    public static final int TYPE_MIGRATE = 1002;// 迁移

    private int type;

    private T data;

    public static <T> PluginTypeModel<T> build(int type, T data) {
        PluginTypeModel<T> model = new PluginTypeModel<>();
        model.type = type;
        model.data = data;
        return model;
    }

    public synchronized int getType() {
        return type;
    }

    public synchronized void setType(int type) {
        this.type = type;
    }

    public synchronized T getData() {
        return data;
    }

    public synchronized void setData(T data) {
        this.data = data;
    }
}
