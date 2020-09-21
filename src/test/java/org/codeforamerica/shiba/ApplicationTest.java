package org.codeforamerica.shiba;

import org.codeforamerica.shiba.application.Application;
import org.codeforamerica.shiba.pages.Feedback;
import org.codeforamerica.shiba.pages.Sentiment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {
    @Test
    void shouldAddFeedback() {
        Application application = Application.builder().build();

        Sentiment sentiment = Sentiment.HAPPY;
        String feedback = "someFeedback";
        Application updatedApplication = application.addFeedback(new Feedback(sentiment, feedback));

        assertThat(updatedApplication.getSentiment()).isEqualTo(sentiment);
        assertThat(updatedApplication.getFeedback()).isEqualTo(feedback);
    }

    @Test
    void shouldNotReplaceExistingSentiment_whenSentimentIsNull() {
        Sentiment originalSentiment = Sentiment.HAPPY;
        Application application = Application.builder()
                .sentiment(originalSentiment)
                .build();

        Application updatedApplication = application.addFeedback(new Feedback(null, null));

        assertThat(updatedApplication.getSentiment()).isEqualTo(originalSentiment);
    }

    @Test
    void shouldNotReplaceExistingFeedbackText_whenFeedbackTextIsNull() {
        String originalFeedbackText = "someFeedback";
        Application application = Application.builder()
                .feedback(originalFeedbackText)
                .build();

        Application updatedApplication = application.addFeedback(new Feedback(null, null));

        assertThat(updatedApplication.getFeedback()).isEqualTo(originalFeedbackText);
    }

    @Test
    void shouldNotReplaceExistingFeedbackText_whenFeedbackTextIsEmpty() {
        String originalFeedbackText = "someFeedback";
        Application application = Application.builder()
                .feedback(originalFeedbackText)
                .build();

        Application updatedApplication = application.addFeedback(new Feedback(null, ""));

        assertThat(updatedApplication.getFeedback()).isEqualTo(originalFeedbackText);
    }
}