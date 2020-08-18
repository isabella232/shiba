package org.codeforamerica.shiba.pages.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codeforamerica.shiba.inputconditions.Condition;
import org.codeforamerica.shiba.pages.FormInputTemplate;
import org.codeforamerica.shiba.pages.PageTemplate;
import org.codeforamerica.shiba.pages.config.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@EqualsAndHashCode(callSuper = true)
@Data
public class PagesData extends HashMap<String, PageData> {
    public PagesData() {
        super();
    }

    public PagesData(Map<String, PageData> map) {
        super(map);
    }
    public PageData getPage(String pageName) {
        return get(pageName);
    }

    public PageData getPageDataOrDefault(String pageName, PageConfiguration pageConfiguration) {
        PageData defaultPageData = PageData.initialize(pageConfiguration);

        return this.getOrDefault(pageName, defaultPageData);
    }

    public void putPage(String pageName, PageData pageData) {
        this.put(pageName, pageData);
    }

    public DatasourcePages getDatasourcePagesBy(List<PageDatasource> datasources) {
        return new DatasourcePages(datasources.stream()
                .filter(datasource -> datasource.getPageName() != null)
                .map(datasource -> Map.entry(
                        datasource.getPageName(),
                        getOrDefault(datasource.getPageName(), new PageData())))
                .collect(toMap(Entry::getKey, Entry::getValue)));
    }

    public boolean shouldSkip(PageWorkflowConfiguration pageWorkflowConfiguration) {
        Condition skipCondition = pageWorkflowConfiguration.getSkipCondition();
        if (skipCondition == null) {
            return false;
        }
        @NotNull DatasourcePages datasourcePages = this.getDatasourcePagesBy(pageWorkflowConfiguration.getDatasources());
        return datasourcePages.satisfies(skipCondition);
    }

    private String resolve(PageWorkflowConfiguration pageWorkflowConfiguration, Value value) {
        if (value == null) {
            return "";
        }
        return value.getConditionalValues().stream()
                .filter(conditionalValue -> {
                    Objects.requireNonNull(pageWorkflowConfiguration.getDatasources(),
                            "Configuration mismatch! Conditional value cannot be evaluated without a datasource.");
                    DatasourcePages datasourcePages = this.getDatasourcePagesBy(pageWorkflowConfiguration.getDatasources());
                    return datasourcePages.satisfies(conditionalValue.getCondition());
                })
                .findFirst()
                .map(ConditionalValue::getValue)
                .orElse(value.getValue());
    }

    public PageTemplate evaluate(PageWorkflowConfiguration pageWorkflowConfiguration) {
        PageConfiguration pageConfiguration = pageWorkflowConfiguration.getPageConfiguration();
        DatasourcePages datasourcePages = this.getDatasourcePagesBy(pageWorkflowConfiguration.getDatasources());

        return new PageTemplate(
                pageConfiguration.getInputs().stream()
                        .filter(input -> Optional.ofNullable(input.getCondition())
                                .map(datasourcePages::satisfies)
                                .orElse(true))
                        .map(this::convert).collect(Collectors.toList()),
                pageConfiguration.getName(),
                resolve(pageWorkflowConfiguration, pageConfiguration.getPageTitle()),
                resolve(pageWorkflowConfiguration, pageConfiguration.getHeaderKey()),
                pageConfiguration.getHeaderHelpMessageKey(),
                pageConfiguration.getPrimaryButtonTextKey(),
                pageConfiguration.getHasPrimaryButton(),
                pageConfiguration.getContextFragment()
        );
    }

    private FormInputTemplate convert(FormInput formInput) {
        return new FormInputTemplate(
                formInput.getType(),
                formInput.getName(),
                formInput.getCustomInputFragment(),
                formInput.getPromptMessage(),
                formInput.getHelpMessageKey(),
                formInput.getValidationErrorMessageKey(),
                formInput.getOptions(),
                formInput.getFollowUps().stream().map(this::convert).collect(Collectors.toList()),
                formInput.getFollowUpsValue(),
                formInput.getReadOnly(),
                formInput.getDefaultValue(),
                formInput.getMax(),
                formInput.getMin()
        );
    }
}
