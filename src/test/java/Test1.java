import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

public class Test1 {

    private final String IMAGE_SAVE_PATH = "./ArticleImages/";

    @Test
    public void test1_element_addition_1() throws InterruptedException, IOException {

        // Initialize WebDriver with options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("--disable-popup-blocking");
        // options.addArguments("--headless"); // Run in headless mode for automation
        WebDriver driver = new ChromeDriver(options);
        new File(IMAGE_SAVE_PATH).mkdirs();
        System.out.println("Started session");
        driver.get("https://elpais.com/");
        // driver.manage().window().maximize();
        Thread.sleep(2000);

        handleAcceptButton(driver);

        // Navigate to the Opinion section
        WebElement opinionLink = driver.findElement(By.linkText("Opinión"));
        opinionLink.click();

        // Step 3: Get the first five articles and their links
        List<WebElement> articles = driver.findElements(By.xpath("//article/header/h2/a"));
        List<String> articleLinks = articles.stream()
                .limit(5)
                .map(a -> a.getAttribute("href"))
                .collect(Collectors.toList());

        List<String> titles = new ArrayList<>();
        System.out.println("articles:" + articleLinks);

        // Scrape titles, content, and download images
        for (String link : articleLinks) {
            driver.get(link);

            // Scrape title and content
            Thread.sleep(2000);
            String title = null;
            String content = null;

            WebElement articleElement = driver.findElement(By.tagName("article"));

            // Find h1 and h2 within the article element
            title = articleElement.findElement(By.tagName("h1")).getText();
            content = articleElement.findElement(By.tagName("h2")).getText();

            System.out.println("\n");
            System.out.println("------------------Article Starts------------------");
            System.out.println("\n");
            System.out.println("Title: " + title);
            System.out.println("\n");
            System.out.println("Content: " + content);
            System.out.println("\n");
            System.out.println("------------------Article Ends------------------");

            // Save title to list
            titles.add(title);

            // Download image
            WebElement imageElement = driver.findElement(By.cssSelector("span.a_m_w img"));
            String imageUrl = imageElement.getAttribute("src");
            downloadImage(imageUrl, IMAGE_SAVE_PATH + title.replaceAll("\\W+", "_") + ".jpg");
        }

        // Translate titles to English
        System.out.println("titles:" + titles);
        List<String> translatedTitles = new ArrayList<>();
        for (String title : titles) {
            String translated = translateText(title, "es", "en");
            System.out.println("Translated Title: " + translated);
            System.out.println("\n");
            translatedTitles.add(translated);
        }

        // Find repeated words in translated titles
        Map<String, Integer> wordCount = new HashMap<>();
        for (String title : translatedTitles) {
            String[] words = title.toLowerCase().split("\\W+");
            for (String word : words) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        wordCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 2)
                .forEach(
                        entry -> System.out.println("Word: " + entry.getKey() + ", Count: " + entry.getValue() + "\n"));

        if (driver != null) {
            driver.quit();
        }
        /*
         * Once you are outside this code, the list would be empty
         */
    }

    private void handleAcceptButton(WebDriver driver) {
        try {
            // Try the first element
            WebElement acceptButton = driver.findElement(By.cssSelector("a.pmConsentWall-button"));
            acceptButton.click();
            System.out.println("Clicked the first accept button.");
        } catch (Exception e1) {
            try {
                // If the first is not found, try the second element
                WebElement alternateAcceptButton = driver.findElement(By.cssSelector("button[id='didomi-notice-agree-button'] span"));
                alternateAcceptButton.click();
                System.out.println("Clicked the alternate accept button.");
            } catch (Exception e2) {
                // Log if neither element is found
                System.err.println("No accept button found on the page.");
            }
        }
    }

    private void downloadImage(String imageUrl, String savePath) {
        try (InputStream in = new URL(imageUrl).openStream(); FileOutputStream out = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.err.println("Failed to download image: " + e.getMessage());
        }
    }

    public static String translateText(String textToTranslate, String fromLanguage, String toLanguage)
            throws IOException {
        // URL of the translation API
        String url = "https://rapid-translate-multi-traduction.p.rapidapi.com/t";

        // Create HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Prepare the POST request
        HttpPost request = new HttpPost(url);

        // Set the necessary headers
        request.addHeader("Content-Type", "application/json");
        request.addHeader("x-rapidapi-host", "rapid-translate-multi-traduction.p.rapidapi.com");
        request.addHeader("x-rapidapi-key", "YOUR_API_KEY");

        // Prepare the JSON payload
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("from", fromLanguage);
        jsonPayload.put("to", toLanguage);
        jsonPayload.put("q", textToTranslate);

        // Set the entity (body) of the request with the JSON payload
        StringEntity entity = new StringEntity(jsonPayload.toString());
        request.setEntity(entity);

        // Execute the request
        HttpResponse response = httpClient.execute(request);

        // Get the response body as a string
        String responseString = EntityUtils.toString(response.getEntity());

        // Parse the response to extract the translated text
        String translatedText = "";
        try {
            // Since the response might be a JSON array, handle it as such
            JSONArray jsonArray = new JSONArray(responseString);

            // Check if the response array has any elements
            if (jsonArray.length() > 0) {
                translatedText = jsonArray.getString(0); // Get the first element of the array
            } else {
                System.out.println("The translated array is empty.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Close the HTTP client
        httpClient.close();

        return translatedText;
    }
}
