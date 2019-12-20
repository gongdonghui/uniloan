package com.sup.cms.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.cms.bean.po.AuthResourceBean;
import com.sup.cms.bean.po.AuthRoleResourceBean;
import com.sup.cms.bean.po.AuthUserRoleBean;
import com.sup.cms.mapper.*;
import com.sup.cms.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 注册拦截器
 *
 * @author kouichi
 * @date 2018/10/3
 */
@Configuration
@Slf4j
public class WebAppConfig implements WebMvcConfigurer {


    private final String REDIS_CMS_PRE = "CMS-";    // 注意跟 AuthorityController中定义一致

    /**
     * 开启拦截器中的autowired,否则会注入失败
     */
    @Bean
    public BizInterceptor bizInterceptor() {
        return new BizInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自定义拦截器，添加拦截路径和排除拦截路径
        registry.addInterceptor(bizInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/user/doLogin", "/auth/user/logout");
    }

    public class BizInterceptor implements HandlerInterceptor {

        @Autowired
        private StringRedisTemplate redis;
        @Autowired
        private AuthUserRoleBeanMapper userRoleBeanMapper;
        @Autowired
        private AuthRoleResourceBeanMapper roleResourceBeanMapper;
        @Autowired
        private AuthResourceBeanMapper resourceBeanMapper;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setCharacterEncoding("utf-8");

            String token = request.getHeader("token");

            //根据token 可以从redis取到user信息 通过user信息可以找到相关权限

            try {
                //1.检查是否登录 token存在就代表已登录
                if (!redis.hasKey(REDIS_CMS_PRE + token)) {
                    writeResponse(response, "用户未登录");
                    return false;
                }
                //1.1 登录成功时 刷新token有效时间
                redis.expire(REDIS_CMS_PRE + token, 30, TimeUnit.MINUTES);

                //2.是否有该访问权限
                String requestURI = request.getRequestURI();
                String userId = redis.opsForValue().get(token);
                //后台系统 访问量不高 暂不考虑使用缓存
                boolean flag = false;
                QueryWrapper<AuthUserRoleBean> qw1 = new QueryWrapper<>();
                qw1.eq("user_id", userId);
                List<AuthUserRoleBean> roleList = userRoleBeanMapper.selectList(qw1);
                if (null == roleList || roleList.size() == 0) {
                    writeResponse(response, "无权限");
                    return false;
                }
                outer:
                for (AuthUserRoleBean x : roleList) {
                    QueryWrapper<AuthRoleResourceBean> qw2 = new QueryWrapper<>();
                    qw2.eq("role_id", x.getRoleId());
                    List<AuthRoleResourceBean> resourceList = roleResourceBeanMapper.selectList(qw2);
                    if (null != resourceList && resourceList.size() > 0) {
                        for (AuthRoleResourceBean xx : resourceList) {
                            AuthResourceBean bean = resourceBeanMapper.selectById(xx.getResourceId());
                            //命中说明有权限访问 退出整个循环
                            if (requestURI.startsWith(bean.getUrl())) {
                                flag = true;
                                break outer;
                            }
                        }
                    }
                }
                if (!flag) {
                    writeResponse(response, "无权限");
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        private void writeResponse(HttpServletResponse response, String msg) throws Exception {
            response.getWriter().write(ResponseUtil.failed(msg));
        }
    }

}