package org.codeforamerica.shiba.output;

import org.codeforamerica.shiba.pages.ApplicationData;
import org.codeforamerica.shiba.pages.FormInput;
import org.codeforamerica.shiba.pages.InputData;
import org.codeforamerica.shiba.pages.PagesConfiguration;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.codeforamerica.shiba.output.ApplicationInputsMapper.formInputTypeToApplicationInputType;

@Component
public class OneToOneApplicationInputsMapper implements ApplicationInputsMapper {
    private final PagesConfiguration pagesConfiguration;

    public OneToOneApplicationInputsMapper(PagesConfiguration pagesConfiguration) {
        this.pagesConfiguration = pagesConfiguration;
    }

    @Override
    public List<ApplicationInput> map(ApplicationData data) {
        return pagesConfiguration.getPageDefinitions().stream()
                .flatMap(pageConfiguration -> pageConfiguration.getFlattenedInputs().stream()
                        .map(input -> new AbstractMap.SimpleEntry<>(pageConfiguration.getName(), input)))
                .filter(entry -> data.getPagesData().getPage(entry.getKey()) != null)
                .map(entry -> {
                    FormInput formInput = entry.getValue();
                    InputData inputData = data.getPagesData().getPage(entry.getKey()).get(formInput.getName());
                    return new ApplicationInput(
                            entry.getKey(),
                            inputData.getValue(),
                            formInput.getName(),
                            formInputTypeToApplicationInputType(formInput.getType()));
                })
                .collect(toList());
    }
}
