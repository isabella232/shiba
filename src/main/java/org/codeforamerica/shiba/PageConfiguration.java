package org.codeforamerica.shiba;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;

@Configuration
@PropertySource(value = "classpath:pages-config.yaml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "pages")
public class PageConfiguration extends HashMap<String, Page> {
}