package org.javaloong.kongmink.petclinic.rest.auth.internal;

import io.buji.pac4j.env.Pac4jIniEnvironment;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

import javax.servlet.Filter;

import static java.lang.Thread.currentThread;
import static org.javaloong.kongmink.petclinic.rest.auth.internal.AuthShiroFilter.CONTEXT_NAME;

@Component(service = Filter.class, immediate = true)
@HttpWhiteboardContextSelect("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=" + CONTEXT_NAME + ")")
@HttpWhiteboardFilterPattern("/*")
public class AuthShiroFilter extends AbstractShiroFilter {

    public static final String CONTEXT_NAME  = "context.for" + RESTConstants.JAX_RS_NAME;

    private WebEnvironment env;

    @Override
    public void init() {
        if(env == null) {
            env = createWebEnvironment();
        }
        this.setSecurityManager(env.getWebSecurityManager());
        FilterChainResolver resolver = env.getFilterChainResolver();
        if (resolver != null) {
            this.setFilterChainResolver(resolver);
        }
    }

    private WebEnvironment createWebEnvironment() {
        final ClassLoader ldr = currentThread().getContextClassLoader();
        currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
            IniWebEnvironment environment = new KarafIniWebEnvironment();
            environment.setServletContext(getServletContext());
            environment.init();
            return environment;
        } finally {
            currentThread().setContextClassLoader(ldr);
        }
    }

    @Override
    public void destroy() {
        LifecycleUtils.destroy(env);
    }
}
