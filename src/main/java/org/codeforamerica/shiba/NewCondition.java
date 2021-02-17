package org.codeforamerica.shiba;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.codeforamerica.shiba.inputconditions.ValueMatcher;
import org.codeforamerica.shiba.pages.data.PageData;
import org.codeforamerica.shiba.pages.data.PagesData;

public interface NewCondition {
    @JsonIgnore
    ValueMatcher matcher = ValueMatcher.CONTAINS;

    boolean matches(PageData pageData, PagesData pagesData);

    boolean appliesForAllIterations();

    String getPageName();

    String getInput();
}
