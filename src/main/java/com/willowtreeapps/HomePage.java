package com.willowtreeapps;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created on 5/23/17.
 */
public class HomePage extends BasePage {


    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * The label that displays the name of the person to be found within the gallery of photos.
     *
     * @return WebElement - the label containing the text, name of the person to be guessed.
     */
    private WebElement nameToBeFound() {
        return driver.findElement(By.id("name"));
    }

    /**
     * The label that displays the 'streak' stat.
     *
     * @return WebElement - the label containing the number of consecutive correct guesses.
     */
    private WebElement streakCounter() {
        return driver.findElement(By.className("streak"));
    }

    /**
     * The label that displays the 'tries' stat.
     *
     * @return WebElement - the label containing the total number of tries.
     */
    private WebElement triesCounter() {
        return driver.findElement(By.className("attempts"));
    }

    /**
     * The label that displays the 'correct' stat.
     *
     * @return WebElement - the label containing the total number of correct guesses.
     */
    private WebElement correctCounter() {
        return driver.findElement(By.className("correct"));
    }

    /**
     * A helper method which allows time for the page to appear/load.
     *
     * If after the 6 second wait time the name to be guessed is not present the method will fail on an assertion.
     */
    private void waitForPageToLoad() {
        sleep(6000);

        Assert.assertTrue(nameToBeFound().isDisplayed());
    }

    /**
     * A helper method which finds the photo within the gallery that matches a specified name.
     *
     * @param name String - the name of the person whose photo is to be found.
     * @return WebElement - the clickable photo element belonging to the name specified.
     */
    private WebElement findPhoto(final String name) {
        final String pathToElement = String.format("//div[text()='%s']/..", name);

        return driver.findElement(By.xpath(pathToElement));
    }

    /**
     * A helper method which compiles a list of the names associated with the photos displayed in the gallery.
     *
     * @return List - the names of those individuals whose photos are currently displayed in the gallery.
     */
    private List<String> retrieveGallery() {
        final List<String> allNames = new ArrayList<>();
        final List<WebElement> gallery = driver.findElements(By.className("name"));

        // For each name element in the gallery, retrieve its text and add it to the list of names.
        for (WebElement name : gallery) {
            allNames.add(name.getText());
        }

        return allNames;
    }

    /**
     * Retrieve the displayed value of the 'streak' counter.
     *
     * @return int - the value of the displayed number.
     */
    private int getCurrentStreakCount() {
        return Integer.parseInt(streakCounter().getText());
    }

    /**
     * Retrieve the displayed value of the 'tries' counter.
     *
     * @return int - the value of the displayed number.
     */
    private int getCurrentTriesCount() {
        return Integer.parseInt(triesCounter().getText());
    }

    /**
     * Retrieve the displayed value of the 'correct' counter.
     *
     * @return int - the value of the displayed number.
     */
    private int getCurrentCorrectCount() {
        return Integer.parseInt(correctCounter().getText());
    }

    /**
     * Verify that the 'streak' counter increases when the user clicks the correct photo.
     *
     * This is done by retrieving the name of the individual to be found with the {@link #nameToBeFound()} element,
     * whose text value is then passed through {@link #findPhoto(String)}. The findPhoto() method returns the photo
     * element associated with the desired person. The photo element is then clicked. This method only asserts that the
     * streak counter increases, we are not concerned with how much.
     */
    public void validateClickingCorrectPhotoIncreasesStreakCounter() {
        waitForPageToLoad();

        // Retrieve the current streak count.
        int startingCount = getCurrentStreakCount();

        // Find the photo that is correct and click it.
        findPhoto(nameToBeFound().getText()).click();

        waitForPageToLoad();

        // Verify that the streak count has increased.
        Assert.assertTrue(getCurrentStreakCount() > startingCount);
    }

    /**
     * Verify that after a streak of correct guesses, the 'streak' counter resets to 0 upon the next incorrect guess.
     *
     * This is done by selecting the correct photo for 3 consecutive attempts, which should set a streak. On the 4th
     * attempt we will click a photo that is not the correct one. Then we'll retrieve the streak count and assert that
     * it is now reset to 0.
     */
    public void validateStreakCounterResetsAfterIncorrectSelection() {
        final int resetValue     = 0;
        final int expectedStreak = 3;

        waitForPageToLoad();

        // Retrieve the current, starting steak count.
        final int startingStreak = getCurrentStreakCount();

        // Process 3 consecutive attempts, selecting the correct photo so that a streak is started.
        for (int i = 1; i <= expectedStreak; i++) {
            // Find the photo that is correct and click it.
            findPhoto(nameToBeFound().getText()).click();

            waitForPageToLoad();

            // Verify that the streak counter increments.
            int countAfter = getCurrentStreakCount();
            Assert.assertTrue(countAfter > startingStreak);
        }

        // Verify that the streak is 3 more than the starting value.
        Assert.assertEquals(startingStreak + expectedStreak, getCurrentStreakCount());

        /*
         * Now select a name that is not the correct option. To do this we will gather the list of names in the gallery
         * and remove the correct option. Then we'll click the first photo that is not the correct one.
         */
        List<String> allIncorrectOptions = retrieveGallery();
        allIncorrectOptions.remove(nameToBeFound().getText());
        findPhoto(allIncorrectOptions.get(0)).click();

        // Verify that the streak counter is now reset.
        Assert.assertEquals(resetValue, getCurrentStreakCount());
    }

    /**
     * Verify that the 'tries' and 'correct' counters are incrementing as expected.
     *
     * This method will complete 10 random attempts at the game and keep a count of how many of the random selections
     * are correct. This is done by generating a random number between 0 and the last index of the number of photos
     * remaining in the gallery after any one of the attempts. The photo at that random index will then be matched to
     * the name of the person to be guessed. If it matches, a counter is incremented for number of correct guesses and
     * the tries continue. If the random guess does not match, then we'll remove that name from the gallery and generate
     * a new random index to continue. At the end of the 10 attempts we will compare the actual, displayed value of the
     * 'correct' counter with that of the number we expect from the attempts and verify that the 'tries' counter has
     * increased by 10.
     */
    public void validateTriesAndCorrectSelectionCounters() {
        waitForPageToLoad();

        // Setup the expected data.
        int attempts                 = 10;
        int numCorrectDuringAttempts = 0;

        // Set up the running counter of the number of tries and correct selections.
        int startingNumOfTries = getCurrentTriesCount();
        int startingNumCorrect = getCurrentCorrectCount();

        // Keep up with the gallery of selections.
        List<String> remainingPhotosToSelect = retrieveGallery();

        // Attempt 10 tries.
        for (int i = 1; i <= attempts; i++) {
            // Generate a random number between 0 and the number of remaining photos available to select.
            int randomSelection = new Random().nextInt(remainingPhotosToSelect.size());

            // If the random selection is that of the correct photo, increment the correct number counter.
            if (remainingPhotosToSelect.get(randomSelection).equals(nameToBeFound().getText())) {
                numCorrectDuringAttempts++;

                // Click the photo.
                findPhoto(remainingPhotosToSelect.get(randomSelection)).click();

                // Allow time for the page to load then retrieve the new gallery.
                waitForPageToLoad();
                remainingPhotosToSelect = retrieveGallery();

            } else {
                // Otherwise, click the random selection and continue through the number of attempts.
                findPhoto(remainingPhotosToSelect.get(randomSelection)).click();
                remainingPhotosToSelect.remove(randomSelection); // Remove the already selected photo from the running list.
            }
        }

        // Verify that the tries counter incremented correctly.
        final int expectedTotalNumOfTries = startingNumOfTries + attempts;
        Assert.assertEquals(expectedTotalNumOfTries, getCurrentTriesCount());

        // Verify that the 'X correct' counter incremented correctly.
        final int expectedTotalNumOfCorrectSelections = startingNumCorrect + numCorrectDuringAttempts;
        Assert.assertEquals(expectedTotalNumOfCorrectSelections, getCurrentCorrectCount());
    }

    /**
     * Verify that the name to be guessed and the gallery of photos changes after a correct guess.
     *
     * This is done by storing the initial name to be guessed and all of the names in the photo gallery at the start of
     * the test. The correct photo will then be selected and assertions will be made against the previous name and
     * gallery to ensure that they are not the same.
     */
    public void validateNameAndGalleryChangesAfterCorrectGuess() {
        waitForPageToLoad();

        // Store the starting name to be guessed.
        final String firstNameToGuess = nameToBeFound().getText();

        // Retrieve all of the names in the gallery.
        final List<String> firstGallery = retrieveGallery();

        // Find and click on the correct photo. Verify that the web app responds as expected.
        final int expectedStreak = getCurrentStreakCount() + 1;
        findPhoto(nameToBeFound().getText()).click();
        Assert.assertEquals(expectedStreak, getCurrentStreakCount());

        waitForPageToLoad();

        // Verify that the name to be guessed has changed to a new name.
        Assert.assertFalse("Expected a new name to appear.", firstNameToGuess.equals(nameToBeFound().getText()));

        // Verify that the gallery of photos has changed.
        Assert.assertNotEquals("Expected the gallery of photos to change.", firstGallery, retrieveGallery());
    }

    public void validateTitleIsPresent() {
        WebElement title = driver.findElement(By.cssSelector("h1"));
        Assert.assertTrue(title != null);
    }

    public void validateClickingFirstPhotoIncreasesTriesCounter() {
        //Wait for page to load
        waitForPageToLoad();

        int count = getCurrentTriesCount();

        driver.findElement(By.className("photo")).click();

        waitForPageToLoad();

        int countAfter = getCurrentTriesCount();

        Assert.assertTrue(countAfter > count);
    }

}
