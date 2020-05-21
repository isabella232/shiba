package org.codeforamerica.shiba;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension.class)
abstract class AbstractBasePageTest {
    protected RemoteWebDriver driver;
    protected Path path;

    @LocalServerPort
    protected String localServerPort;

    @SuppressWarnings("InnerClassMayBeStatic")
    @TestConfiguration
    class PageInteractionTestConfiguration {
        @Bean
        public CustomScopeConfigurer customScopeConfigurer() {
            CustomScopeConfigurer configurer = new CustomScopeConfigurer();
            configurer.addScope(WebApplicationContext.SCOPE_SESSION, new SimpleThreadScope());
            return configurer;
        }
    }

    @BeforeEach
    void setUp() {
        try {
            ChromeOptions options = new ChromeOptions();
            path = Files.createTempDirectory("");
            HashMap<String, Object> chromePrefs = new HashMap<>();
//        chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory", path.toString());
            options.setExperimentalOption("prefs", chromePrefs);
            options.addArguments("--headless");
            driver = new ChromeDriver(options);
        } catch (Exception e) {

        }
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @SuppressWarnings("unused")
    public static void takeSnapShot(WebDriver webdriver, String fileWithPath) throws Exception {
        TakesScreenshot screenshot = ((TakesScreenshot) webdriver);
        Path sourceFile = screenshot.getScreenshotAs(OutputType.FILE).toPath();
        Path destinationFile = new File(fileWithPath).toPath();
        Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
    }
}