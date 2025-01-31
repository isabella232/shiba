package org.codeforamerica.shiba.application.parsers;

import org.codeforamerica.shiba.output.caf.SnapExpeditedEligibilityParameters;
import org.codeforamerica.shiba.output.caf.JobIncomeInformation;
import org.codeforamerica.shiba.pages.data.ApplicationData;
import org.codeforamerica.shiba.pages.data.PagesData;
import org.codeforamerica.shiba.pages.data.Subworkflow;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SnapExpeditedEligibilityParser extends ApplicationDataParser<Optional<SnapExpeditedEligibilityParameters>> {
    private final ApplicationDataParser<List<JobIncomeInformation>> jobIncomeInformationParser;

    public SnapExpeditedEligibilityParser(ParsingConfiguration parsingConfiguration,
                                          ApplicationDataParser<List<JobIncomeInformation>> jobIncomeInformationParser) {
        super(parsingConfiguration);
        this.jobIncomeInformationParser = jobIncomeInformationParser;
    }

    public Optional<SnapExpeditedEligibilityParameters> parse(ApplicationData applicationData) {
        Map<String, PageInputCoordinates> coordinatesMap = parsingConfiguration.get("snapExpeditedEligibility").getPageInputs();
        PagesData pagesData = applicationData.getPagesData();

        List<String> requiredPages = coordinatesMap.values().stream()
                .filter(PageInputCoordinates::getRequired)
                .map(PageInputCoordinates::getPageName)
                .collect(Collectors.toList());
        if (!pagesData.keySet().containsAll(requiredPages)) {
            return Optional.empty();
        }

        boolean hasJobsSubworkflow = applicationData.getSubworkflows().get("jobs") != null;
        if (hasJobsSubworkflow) {
            Subworkflow jobsSubworkflow = applicationData.getSubworkflows().get("jobs");
            boolean allIterationsContainThirtyDayEstimate = jobsSubworkflow.stream().allMatch(iteration -> iteration.getPagesData().containsKey("lastThirtyDaysJobIncome"));
            if (allIterationsContainThirtyDayEstimate) {
                boolean shouldNotDetermineEligibilityFromAllBlankEstimates = jobsSubworkflow.stream().allMatch(iteration -> iteration.getPagesData().getPage("lastThirtyDaysJobIncome").get("lastThirtyDaysJobIncome").getValue(0).isBlank());
                if (shouldNotDetermineEligibilityFromAllBlankEstimates) {
                    return Optional.empty();
                }
            }
        }

        double assets = getDouble(applicationData, coordinatesMap.get("assets"));
        double last30DaysIncome = getDouble(applicationData, coordinatesMap.get("income"));

        double housingCosts = getDouble(applicationData, coordinatesMap.get("housingCosts"));
        boolean isMigrantWorker = Boolean.parseBoolean(pagesData.getPage(coordinatesMap.get("migrantWorker").getPageName())
                .get(coordinatesMap.get("migrantWorker").getInputName()).getValue(0));
        @NotNull List<String> utilityExpensesSelections = pagesData.getPage(coordinatesMap.get("utilityExpensesSelections").getPageName())
                .get(coordinatesMap.get("utilityExpensesSelections").getInputName()).getValue();

        boolean applyingForSnap = pagesData.getPage("choosePrograms").get("programs").getValue(0).contains("SNAP");
        return Optional.of(new SnapExpeditedEligibilityParameters(assets, last30DaysIncome, jobIncomeInformationParser.parse(applicationData), isMigrantWorker, housingCosts, utilityExpensesSelections, applyingForSnap));
    }
}
