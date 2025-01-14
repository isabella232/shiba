package org.codeforamerica.shiba.pages.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;

public class ApplicationConfigurationFactory implements FactoryBean<ApplicationConfiguration> {
    @Value("${pagesConfig:pages-config.yaml}") String configPath;

    @Override
    public ApplicationConfiguration getObject() {
        ClassPathResource classPathResource = new ClassPathResource(configPath);

        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(false);
        loaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
        loaderOptions.setAllowRecursiveKeys(true);

        Yaml yaml = new Yaml(new Constructor(ApplicationConfiguration.class), new Representer(), new DumperOptions(), loaderOptions);
        ApplicationConfiguration appConfig = null;
        try {
            appConfig = yaml.load(classPathResource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return appConfig;
    }

    @Override
    public Class<?> getObjectType() {
        return ApplicationConfiguration.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
