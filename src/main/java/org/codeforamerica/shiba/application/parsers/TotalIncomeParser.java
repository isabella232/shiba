package org.codeforamerica.shiba.application.parsers;

import org.codeforamerica.shiba.output.caf.JobIncomeInformation;
import org.codeforamerica.shiba.output.caf.TotalIncome;
import org.codeforamerica.shiba.pages.data.ApplicationData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TotalIncomeParser extends ApplicationDataParser<TotalIncome> {
    private final ApplicationDataParser<List<JobIncomeInformation>> grossIncomeParser;

    public TotalIncomeParser(ParsingConfiguration parsingConfiguration,
                             ApplicationDataParser<List<JobIncomeInformation>> grossIncomeParser) {
        super(parsingConfiguration);
        this.grossIncomeParser = grossIncomeParser;
    }

    @Override
    public TotalIncome parse(ApplicationData applicationData) {
        ParsingCoordinates expeditedEligibilityConfiguration = parsingConfiguration.get("snapExpeditedEligibility");
        double last30DaysIncome = getDouble(applicationData, expeditedEligibilityConfiguration.getPageInputs().get("income"));
        return new TotalIncome(last30DaysIncome, grossIncomeParser.parse(applicationData));
    }
}
