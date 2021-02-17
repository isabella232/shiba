package org.codeforamerica.shiba.pages.data;

import lombok.Data;
import org.codeforamerica.shiba.NewCondition;
import org.codeforamerica.shiba.application.FlowType;
import org.codeforamerica.shiba.application.parsers.PageInputCoordinates;
import org.codeforamerica.shiba.pages.config.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class ApplicationData {
    private String id;
    private Instant startTime;
    private FlowType flow = FlowType.UNDETERMINED;
    private PagesData pagesData = new PagesData();
    private Subworkflows subworkflows = new Subworkflows();
    private Map<String, PagesData> incompleteIterations = new HashMap<>();
    private Map<String,MultipartFile> uploadedDocuments = new HashMap<>();

    public void setStartTimeOnce(Instant instant) {
        if (startTime == null) {
            startTime = instant;
        }
    }

    public PageData getPageData(String pageName) {
        return this.pagesData.getPage(pageName);
    }

    public String getValue(PageInputCoordinates pageInputCoordinates) {
        return Optional.ofNullable(this.getPageData(pageInputCoordinates.getPageName()))
                .map(pageData -> pageData.get(pageInputCoordinates.getInputName()))
                .filter(inputData -> !inputData.getValue().isEmpty())
                .map(inputData -> inputData.getValue(0))
                .orElse(pageInputCoordinates.getDefaultValue());
    }

    public Subworkflows getSubworkflowsForPageDatasources(List<PageDatasource> pageDatasources) {
        return new Subworkflows(pageDatasources.stream()
                .filter(datasource -> datasource.getGroupName() != null)
                .map(datasource -> Map.entry(
                        datasource.getGroupName(),
                        subworkflows.get(datasource.getGroupName())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public boolean hasRequiredSubworkflows(List<PageDatasource> datasources) {
        return datasources.stream()
                .filter(datasource -> datasource.getGroupName() != null)
                .allMatch(datasource -> getSubworkflows().get(datasource.getGroupName()) != null);
    }

    public NextPage getNextPageName(FeatureFlagConfiguration featureFlags, @NotNull PageWorkflowConfiguration pageWorkflowConfiguration, Integer option) {
        if (pageWorkflowConfiguration.isDirectNavigation()) {
            return pageWorkflowConfiguration.getNextPages().get(option);
        }
        PageData pageData;
        if (pageWorkflowConfiguration.isInAGroup()) {
            pageData = incompleteIterations.get(pageWorkflowConfiguration.getGroupName()).get(pageWorkflowConfiguration.getPageConfiguration().getName());
        } else {
            pageData = pagesData.getPage(pageWorkflowConfiguration.getPageConfiguration().getName());
        }

        if (pageData == null) {
            throw new RuntimeException(String.format("Conditional navigation for %s requires page to have data/inputs.", pageWorkflowConfiguration.getPageConfiguration().getName()));
        }

        return pageWorkflowConfiguration.getNextPages().stream()
                .filter(nextPage -> {
                    NewCondition condition = nextPage.getCondition();
                    if (condition != null) {
                        return condition.matches(pageData, pagesData);
                    }
                    if (nextPage.getFlag() != null) {
                        return featureFlags.get(nextPage.getFlag()) == FeatureFlag.ON;
                    }
                    return true;
                }).findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find suitable next page."));
    }

    public void addUploadedDocument(MultipartFile file) {
        uploadedDocuments.put(file.getOriginalFilename(),file);
    }

    public boolean isCCAPApplication() {
        List<String> applicantPrograms = this.getPagesData().safeGetPageInputValue("choosePrograms", "programs");
        boolean applicantHasCCAP = applicantPrograms.contains("CCAP");
        boolean hasHousehold = this.getSubworkflows().containsKey("household");
        boolean householdHasCCAP = false;
        if (hasHousehold) {
            householdHasCCAP = this.getSubworkflows().get("household").stream().anyMatch(iteration ->
                    iteration.getPagesData().safeGetPageInputValue("householdMemberInfo", "programs").contains("CCAP"));
        }
        return applicantHasCCAP || householdHasCCAP;
    }

    public boolean isCAFApplication() {
        List<String> applicantPrograms = this.getPagesData().safeGetPageInputValue("choosePrograms", "programs");
        List<String> availablePrograms = List.of("SNAP", "CASH", "GRH", "EA");
        boolean applicantIsCAF = availablePrograms.stream().anyMatch(applicantPrograms::contains);
        boolean hasHousehold = this.getSubworkflows().containsKey("household");
        boolean householdIsCAF = false;
        if (hasHousehold) {
            householdIsCAF = this.getSubworkflows().get("household").stream().anyMatch(iteration -> {
                List<String> iterationsPrograms = iteration.getPagesData().safeGetPageInputValue("householdMemberInfo", "programs");
                return availablePrograms.stream().anyMatch(iterationsPrograms::contains);
            });
        }

        return applicantIsCAF || householdIsCAF;
    }
}
