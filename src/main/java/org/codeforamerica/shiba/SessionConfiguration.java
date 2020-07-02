package org.codeforamerica.shiba;

import org.codeforamerica.shiba.metrics.Metrics;
import org.codeforamerica.shiba.pages.PagesData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_SESSION;

@Configuration
public class SessionConfiguration {
    @Bean
    @Scope(
            value = SCOPE_SESSION,
            proxyMode = TARGET_CLASS
    )
    public PagesData data() {
        return new PagesData();
    }

    @Bean
    @Scope(
            value = SCOPE_SESSION,
            proxyMode = TARGET_CLASS
    )
    public Metrics sessionMetadata() {
        return new Metrics();
    }
}
