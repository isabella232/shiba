package org.codeforamerica.shiba;

import org.codeforamerica.shiba.pages.config.FeatureFlag;
import org.codeforamerica.shiba.pages.config.FeatureFlagConfiguration;
import org.codeforamerica.shiba.pages.config.NextPage;

import java.util.ArrayList;
import java.util.List;

public class NewConditionFactory {
    public static List<NewCondition> getConditions(FeatureFlagConfiguration featureFlags, NextPage nextPage) {
        ArrayList<NewCondition> newConditions = new ArrayList<>(List.of());
        NewCondition flagConditions = getFlagConditions(featureFlags, nextPage);

        if (nextPage.getCondition() != null) newConditions.add(nextPage.getCondition());
        if (flagConditions != null) newConditions.add(flagConditions);

        return newConditions;
    }

    private static NewCondition getFlagConditions(FeatureFlagConfiguration featureFlags, NextPage nextPage) {
        FeatureFlag featureFlag = featureFlags.get(nextPage.getFlag());
        if (featureFlag != null) return new FeatureFlagCondition(featureFlag);
        return null;
    }
}
