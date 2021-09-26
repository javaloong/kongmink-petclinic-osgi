package org.javaloong.kongmink.petclinic.rest.auth.internal;

import org.apache.shiro.config.Ini;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

import javax.servlet.Filter;

import static org.javaloong.kongmink.petclinic.rest.auth.internal.AuthShiroFilter.CONTEXT_NAME;

@Component(service = Filter.class, immediate = true)
@HttpWhiteboardContextSelect("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=" + CONTEXT_NAME + ")")
@HttpWhiteboardFilterPattern("/*")
public class AuthShiroFilter extends AbstractShiroFilter {

    public static final String CONTEXT_NAME  = "context.for" + RESTConstants.JAX_RS_NAME;

    private static final Ini INI_FILE = new Ini();

    static {
        // Can't use the Ini.fromResourcePath(String) method because it can't find "shiro.ini" on the classpath in an OSGi context
        INI_FILE.load(AuthShiroFilter.class.getClassLoader().getResourceAsStream("shiro.ini"));
    }

    @Activate
    public void activate() {
        IniWebEnvironment environment = new IniWebEnvironment();
        environment.setIni(INI_FILE);
        environment.setServletContext(getServletContext());
        environment.init();
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) environment.getWebSecurityManager();
        this.setSecurityManager(securityManager);
        this.setFilterChainResolver(environment.getFilterChainResolver());
    }
}
