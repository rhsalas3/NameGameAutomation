package com.willowtreeapps;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class WebTest {

    private WebDriver driver;

    /**
     * Change the prop if you are on Windows or Linux to the corresponding file type
     * The chrome WebDrivers are included on the root of this project, to get the
     * latest versions go to https://sites.google.com/a/chromium.org/chromedriver/downloads
     */
    @Before
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        Capabilities capabilities = DesiredCapabilities.chrome();
        driver = new ChromeDriver(capabilities);
        driver.navigate().to("http://www.ericrochester.com/name-game/");
    }

    @Test
    public void test_validate_title_is_present() {
        new HomePage(driver)
                .validateTitleIsPresent();
    }

    @Test
    public void test_clicking_photo_increases_tries_counter() {
        new HomePage(driver)
                .validateClickingFirstPhotoIncreasesTriesCounter();
    }

    // Verify that the "streak" counter is incrementing on correct selections.
    @Test
    public void test_correct_selection_increases_streak_counter() {
        new HomePage(driver)
                .validateClickingCorrectPhotoIncreasesStreakCounter();
    }

    // Verify that a multiple “streak” counter resets after getting an incorrect answer.
    @Test
    public void test_streak_counter_resets_after_incorrect_selection() {
        new HomePage(driver)
                .validateStreakCounterResetsAfterIncorrectSelection();
    }

    // Verify that after 10 random selections the correct counters are being incremented for tries and correct counters.
    @Test
    public void test_tries_and_correct_counters_increment_correctly() {
        new HomePage(driver)
                .validateTriesAndCorrectSelectionCounters();
    }

    // Verify name and displayed photos change after selecting the correct answer.
    @Test
    public void test_name_and_gallery_changes_after_selecting_correct_answer() {
        new HomePage(driver)
                .validateNameAndGalleryChangesAfterCorrectGuess();
    }

    @After
    public void teardown() {
        driver.quit();
        System.clearProperty("webdriver.chrome.driver");
    }

}
