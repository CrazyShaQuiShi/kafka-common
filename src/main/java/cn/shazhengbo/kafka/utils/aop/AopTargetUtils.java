package cn.shazhengbo.kafka.utils.aop;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * aop目标对象辅助类
 */
public abstract class AopTargetUtils {

    /**
     * 获取 目标对象
     * @param proxy 代理对象
     * @return
     * @throws Exception
     */
    public static <T> T getTarget(T proxy) {

        if(!AopUtils.isAopProxy(proxy)) {
            /**
             * 不是代理对象
             */
            return proxy;
        }

        T target;
        try {
            if (AopUtils.isJdkDynamicProxy(proxy)) {
                target = getJdkDynamicProxyTargetObject(proxy);
            } else { //cglib
                target = getCglibProxyTargetObject(proxy);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return target;
    }

    private static <T> T getCglibProxyTargetObject(T proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        return  (T)((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }

    private static <T> T getJdkDynamicProxyTargetObject(T proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);

        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        return  (T)((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();
    }

}
