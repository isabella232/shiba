package org.codeforamerica.shiba.output.caf;

import org.codeforamerica.shiba.output.ApplicationInput;
import org.codeforamerica.shiba.output.ApplicationInputType;
import org.codeforamerica.shiba.output.ApplicationInputsMapper;
import org.codeforamerica.shiba.pages.ApplicationData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpeditedEligibilityMapper implements ApplicationInputsMapper {
    private final ExpeditedEligibilityDecider eligibilityDecider;

    public ExpeditedEligibilityMapper(ExpeditedEligibilityDecider eligibilityDecider) {
        this.eligibilityDecider = eligibilityDecider;
    }

    @Override
    public List<ApplicationInput> map(ApplicationData data) {
        return List.of(
                new ApplicationInput(
                        "expeditedEligibility",
                        eligibilityDecider.decide(data.getPagesData()) ? List.of("ELIGIBLE") : List.of("NOT_ELIGIBLE"),
                        "expeditedEligibility",
                        ApplicationInputType.ENUMERATED_SINGLE_VALUE
                ));
    }
}