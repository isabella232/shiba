package org.codeforamerica.shiba;

import org.codeforamerica.shiba.pages.config.FeatureFlag;
import org.codeforamerica.shiba.pages.data.PageData;
import org.codeforamerica.shiba.pages.data.PagesData;

public class FeatureFlagCondition implements NewCondition {
    private final boolean on;

    public FeatureFlagCondition(FeatureFlag value) {
        this.on = value.isOn();
    }

    @Override
    public boolean matches(PageData pageData, PagesData pagesData) {
        return false;
    }

    @Override
    public boolean appliesForAllIterations() {
        return false;
    }

    @Override
    public String getPageName() {
        return null;
    }

    @Override
    public String getInput() {
        return null;
    }
}
