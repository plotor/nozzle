package org.zhenchao.nozzle.listener;

import java.util.EventListener;

/**
 * 配置注入监听器
 *
 * @author zhenchao.wang 2017-09-07 16:30:32
 * @version 1.0.0
 */
public interface InjectEventListener extends EventListener {

    /**
     * 前置处理器
     *
     * @param bean
     */
    void before(Object bean);

    /**
     * 后置处理器
     *
     * @param bean
     */
    void after(Object bean);

}
