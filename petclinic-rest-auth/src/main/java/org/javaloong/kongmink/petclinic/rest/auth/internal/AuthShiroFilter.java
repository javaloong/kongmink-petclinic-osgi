package org.javaloong.kongmink.petclinic.rest.auth.internal;

import org.apache.shiro.config.Ini;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.javaloong.kongmink.petclinic.rest.RESTConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.javaloong.kongmink.petclinic.rest.auth.internal.AuthShiroFilter.CONTEXT_NAME;

@Component(service = Filter.class, immediate = true)
@HttpWhiteboardContextSelect("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=" + CONTEXT_NAME + ")")
@HttpWhiteboardFilterPattern("/*")
public class AuthShiroFilter extends AbstractShiroFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthShiroFilter.class);

    public static final String CONTEXT_NAME  = "context.for" + RESTConstants.JAX_RS_NAME;

    @Activate
    public void activate(BundleContext context) throws Exception {
        WebEnvironment env = createWebEnvironment(context);
        this.setSecurityManager(env.getWebSecurityManager());
        FilterChainResolver resolver = env.getFilterChainResolver();
        if (resolver != null) {
            this.setFilterChainResolver(resolver);
        }
    }

    private WebEnvironment createWebEnvironment(BundleContext context) throws IOException {
        IniWebEnvironment environment = new IniWebEnvironment();
        Ini ini = createShiroIni(context.getBundle());
        environment.setIni(ini);
        environment.setServletContext(getServletContext());
        environment.init();
        return environment;
    }

    private Ini createShiroIni(Bundle bundle) throws IOException {
        String shiroIniPath = System.getProperty("karaf.etc") + File.separator + "shiro.ini";
        File f = new File(shiroIniPath);
        if(f.exists()) {
            Ini ini = new Ini();
            String fileBasedIniPath = f.getAbsolutePath();
            log.debug("Attempting an ini load from the file: \"{}\"", fileBasedIniPath);
            ini.loadFromPath(fileBasedIniPath);
            return ini;
        }
        return createShiroIniFromBundle(bundle);
    }

    private Ini createShiroIniFromBundle(Bundle bundle) throws IOException {
        Ini ini = new Ini();
        URL url = bundle.getEntry("shiro.ini");
        ini.load(url.openStream());
        return ini;
    }
}
