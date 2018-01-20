package org.zhenchao.nozzle.listener;

import java.util.EventListener;

/**
 * 配置更新监听器
 *
 * @author zhenchao.wang 2017-09-07 16:31:13
 * @version 1.0.0
 */
public interface UpdateEventListener extends EventListener {

    /**
     * 更新操作前置处理器
     *
     * @param bean The bean that is being updated.
     */
    void before(Object bean);

    /**
     * 更新操作后置处理器
     *
     * @param bean The bean that has been updated.
     */
    void after(Object bean);

}
