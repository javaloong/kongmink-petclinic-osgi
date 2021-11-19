package org.javaloong.kongmink.petclinic.rest.auth.internal;

import io.buji.pac4j.env.Pac4jIniEnvironment;
import org.apache.shiro.config.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class KarafIniWebEnvironment extends Pac4jIniEnvironment {

    private static final Logger LOG = LoggerFactory.getLogger(KarafIniWebEnvironment.class);

    /**
     * The location of shiro.ini relative to ${karaf.home}
     */
    static final String DEFAULT_SHIRO_INI_FILE = "etc" + File.separator + "shiro.ini";

    /**
     * The Shiro-specific prefix used to indicate file based Shiro configuration
     */
    static final String SHIRO_FILE_PREFIX = "file:" + File.separator;

    public KarafIniWebEnvironment() {
        LOG.info("Initializing the Web Environment using {}",
                KarafIniWebEnvironment.class.getName());
    }

    @Override
    public void init() {
        // Initialize the Shiro environment from shiro.ini then delegate to
        // the parent class
        setIni(createShiroIni());
        super.init();
    }

    @Override
    protected Ini getFrameworkIni() {
        return createShiroIni(Pac4jIniEnvironment.class.getClassLoader(), "buji-pac4j-default.ini");
    }

    private Ini createShiroIni() {
        final File f = new File(DEFAULT_SHIRO_INI_FILE);
        if(f.exists()) {
            return createShiroIni(f);
        }
        return createShiroIni(getClass().getClassLoader(), "shiro.ini");
    }

    static Ini createShiroIni(File file) {
        final Ini ini = new Ini();
        final String fileBasedIniPath = createFileBasedIniPath(file.getAbsolutePath());
        LOG.debug("Attempting an ini load from the file: \"{}\"", fileBasedIniPath);
        ini.loadFromPath(fileBasedIniPath);
        return ini;
    }

    static Ini createShiroIni(ClassLoader classLoader, String resourcePath) {
        final URL url = classLoader.getResource(resourcePath);
        LOG.debug("Attempting an ini load from the url: \"{}\"", url);
        return Ini.fromResourcePath("url:" + url);
    }

    static String createFileBasedIniPath(String path) {
        return SHIRO_FILE_PREFIX + path;
    }
}
