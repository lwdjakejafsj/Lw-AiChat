package io.luowei.aichat.common.aop;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.RateLimiter;
import io.luowei.aichat.common.annotation.AccessInterceptor;
import io.luowei.aichat.common.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 接口限流
 * author: luowei
 * date:
 */
@Slf4j
@Aspect
public class IRateLimiter {

    @Resource
    private Cache<String, RateLimiter> loginRecord;

    @Resource
    private Cache<String,Long> blacklist;

    @Pointcut("@annotation(io.luowei.aichat.common.annotation.AccessInterceptor)")
    public void aopPoint() {
    }

    @Around("aopPoint() && @annotation(accessInterceptor)")
    public Object router(ProceedingJoinPoint jp, AccessInterceptor accessInterceptor) throws Throwable {
        String key = accessInterceptor.key();

        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("annotation RateLimiter uId is null！");
        }

        // 获取拦截字段
        String token = getAttrValue(key, jp,accessInterceptor.isDepth());
        log.info("aop attr {}", token);

        String keyAttr = JwtUtil.openId(token);

        //黑名单拦截
        if (!"all".equals(keyAttr)
                && accessInterceptor.blacklistCount() != 0
                && null != blacklist.getIfPresent(keyAttr)
                && blacklist.getIfPresent(keyAttr) > accessInterceptor.blacklistCount()) {
            log.info("限流-黑名单拦截(24h)：{}", keyAttr);
            return fallbackMethodResult(jp, accessInterceptor.fallbackMethod());
        }

        // 获取限流 -> Guava 缓存1分钟
        RateLimiter rateLimiter = loginRecord.getIfPresent(keyAttr);
        if (null == rateLimiter) {
            // 设置每秒允许请求数
            rateLimiter = rateLimiter.create(accessInterceptor.permitsPerSecond());
            loginRecord.put(keyAttr, rateLimiter);
        }

        // 检查许可，是否满足每秒允许请求数，不满足就进行限流计数，达到一定数量加入黑名单
        if (!rateLimiter.tryAcquire()) {
            if (accessInterceptor.blacklistCount() != 0) {
                if (null == blacklist.getIfPresent(keyAttr)) {
                    blacklist.put(keyAttr, 1L);
                } else {
                    blacklist.put(keyAttr, blacklist.getIfPresent(keyAttr) + 1L);
                }
            }
            log.info("限流-超频次拦截：{}", keyAttr);
            return fallbackMethodResult(jp, accessInterceptor.fallbackMethod());
        }

        return jp.proceed();
    }

    /**
     * 调用用户配置的回调方法，当拦截后，返回回调结果。
     */
    private Object fallbackMethodResult(JoinPoint jp, String fallbackMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        Method method = jp.getTarget().getClass().getMethod(fallbackMethod, methodSignature.getParameterTypes());
        return method.invoke(jp.getThis(), jp.getArgs());
    }

    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

    public String getAttrValue(String attr, ProceedingJoinPoint jp,boolean isDepth) {
        Object[] args = jp.getArgs();
        MethodSignature signature = (MethodSignature) jp.getSignature();
        String[] parameterNames = signature.getParameterNames();

        if (args[0] instanceof String) {
            return args[0].toString();
        }

        String filedValue = null;

        if (!isDepth) {
            for (int i = 0; i < signature.getParameterNames().length; i++) {
                String paramName = signature.getParameterNames()[i];
                if (attr.equals(paramName)) {
                    filedValue = (String) args[i];
                    break;
                }
            }
        } else {
            for (Object arg : args) {
                try {
                    if (StringUtils.isNotBlank(filedValue)) {
                        break;
                    }
                    // filedValue = BeanUtils.getProperty(arg, attr);
                    // fix: 使用lombok时，uId这种字段的get方法与idea生成的get方法不同，会导致获取不到属性值，改成反射获取解决
                    filedValue = String.valueOf(this.getValueByName(arg, attr));
                } catch (Exception e) {
                    log.error("获取路由属性值失败 attr：{}", attr, e);
                }
            }
        }

        return filedValue;
    }

    /**
     * 获取对象的特定属性值
     *
     * @param item 对象
     * @param name 属性名
     * @return 属性值
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByName(item, name);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            Object o = field.get(item);
            field.setAccessible(false);
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
     *
     * @param item 对象
     * @param name 属性名
     * @return 该属性对应方法
     */
    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

}
