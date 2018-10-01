package pt.ist.servlet;

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

import com.google.common.base.Splitter;

/**
 * Created by Sérgio Silva (hello@fenixedu.org).
 */
public class CookieCatcherConfiguration {

    @ConfigurationManager(description = "Cookie Catcher Configuration")
    interface ConfigurationProperties  {
        @ConfigurationProperty(key = "CookieCatcher.blacklist", description = "comma separated list of cookie names that are always logged", defaultValue = "")
        String blacklist();
    }


    public static Set<String> blacklist() {
        return Splitter.on(",").splitToList(ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class)
                .blacklist()).stream().map(String::trim).collect(Collectors.toSet());
    }
}
