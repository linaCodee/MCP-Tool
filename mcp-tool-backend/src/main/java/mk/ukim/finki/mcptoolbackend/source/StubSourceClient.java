package mk.ukim.finki.mcptoolbackend.source;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Document;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * TODO(student): Replace this bean with a real client for YOUR assigned website.
 */
@Component
public class StubSourceClient implements SourceClient {
    private final SourceProperties sourceProperties;
    private static final List<String> CATEGORIES=List.of(
            "obrazovanie",
            "nauka",
            "kultura",
            "tehnologija",
            "kolumni",
            "zhivot",
            "kariera"
    );

    public StubSourceClient(SourceProperties sourceProperties) {
        this.sourceProperties = sourceProperties;
    }


    @Override
    public List<FetchedResource> search(String query, int limit) {
        if (query==null || query.isBlank() || limit<=0){
            return List.of();
        }

        List<FetchedResource> fetchedResources=new ArrayList<>();
        Set<String> articleUrls=new LinkedHashSet<>();

        String normalizedQuery=query.trim().toLowerCase(Locale.ROOT);

        ChromeOptions chromeOptions=new ChromeOptions();
        chromeOptions.addArguments("--headless=new");
        chromeOptions.addArguments("--window-size=1920,1080");

        WebDriver webDriver=new ChromeDriver(chromeOptions);

        try {
            String baseUrl= sourceProperties.baseUrl();
            if (baseUrl.endsWith("/")){
                baseUrl=baseUrl.substring(0,baseUrl.length()-1);
            }

            WebDriverWait wait=new WebDriverWait(webDriver,Duration.ofSeconds(10));

            for (String category : CATEGORIES) {
                if (fetchedResources.size()>=limit){
                    break;
                }

                String categoryUrl=baseUrl+"/category/"+category;

                webDriver.get(categoryUrl);

                try {
                    wait.until(
                            ExpectedConditions.presenceOfElementLocated(
                                    By.cssSelector("a[href*='/news/']")
                            )
                    );
                } catch (Exception e){
                    continue;
                }

                findMatchingArticles(webDriver,articleUrls,fetchedResources,normalizedQuery,limit);

                if (fetchedResources.size()>=limit){
                    break;
                }

                for (int i=0;i<2;i++){
                    boolean loadedMore=loadMoreArticles(webDriver);
                    if (!loadedMore){
                        break;
                    }

                    findMatchingArticles(webDriver,articleUrls,fetchedResources,normalizedQuery,limit);
                    if (fetchedResources.size()>=limit){
                        break;
                    }
                }
            }
            return fetchedResources;
        } catch (Exception e){
            e.printStackTrace();
            return List.of();
        } finally {
            webDriver.quit();
        }

    }

    private void findMatchingArticles(
            WebDriver webDriver,
            Set<String> processedUrls,
            List<FetchedResource> fetchedResources,
            String normalizedQuery,
            int limit) {

        List<WebElement> links=webDriver.findElements(
                By.cssSelector("a[href*='/news/']")
        );

        for (WebElement link:links){
            if (fetchedResources.size()>=limit){
                return;
            }

            String url=link.getAttribute("href");
            if (url==null || url.isBlank()){
                continue;
            }
//
//            String linkText=link.getText().strip().toLowerCase(Locale.ROOT);
//
//            if (linkText.isBlank()){
//                continue;
//            }
//
//            if (!linkText.contains(normalizedQuery)){
//                continue;
//            }

            if (!processedUrls.add(url)){
                continue;
            }

            try {
                FetchedResource fetchedResource=fetch(url);
                String searchableText=(fetchedResource.title() + " " + fetchedResource.content()).toLowerCase(Locale.ROOT);
                if (!searchableText.contains(normalizedQuery)){
                    continue;
                }

                fetchedResources.add(fetchedResource);

                System.out.println("MATCH FOUND: "+fetchedResource.title());
            } catch (Exception e){
                System.out.println("FAILED TO FETCH: "+url);
            }
        }
    }
    private boolean loadMoreArticles(WebDriver webDriver){
        try {
            List<WebElement> loadMoreButtons=webDriver.findElements(By.xpath(
                    "//button["+
                            "contains(.,'Прикажи повеќе') "+
                            "or contains(.,'ПРИКАЖИ ПОВЕЌЕ')"+
                            "]"+
                            " | "+
                            "//a["+
                            "contains(.,'Прикажи повеќе') "+
                            "or contains(.,'ПРИКАЖИ ПОВЕЌЕ')"+
                            "]"

            ));
            if (loadMoreButtons.isEmpty()){
                return false;
            }
            WebElement loadMoreButton=loadMoreButtons.get(0);

            if (!loadMoreButton.isDisplayed()){
                return false;
            }

            int oldSize=webDriver.findElements(
                    By.cssSelector("a[href*='/news/']")
            ).size();

            loadMoreButton.click();
            WebDriverWait wait=new WebDriverWait(webDriver,Duration.ofSeconds(5));

            wait.until(d->d.findElements(
                    By.cssSelector("a[href*='/news/']")
            ).size()>oldSize);
            return true;

        } catch (Exception e){
            return false;
        }
    }
    @Override
    public FetchedResource fetch(String url) {
        try {
            Document document=Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get();

            String externalId= URI.create(url).getPath();

            Element titleElement=document.selectFirst(".single-post-title-wrapper h1");
            String title=titleElement!=null ? titleElement.text() : document.title();

            Element contentElement=document.selectFirst(".single-post-content-container");
            String content=contentElement!=null ? contentElement.text() : "";

            Element dateElement=document.selectFirst(".single-post-icons .date span");
            LocalDateTime publishedAt=null;

            if (dateElement!=null){
                String dateText=dateElement.text();

                DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd.MM.yyyy");

                publishedAt= LocalDate.parse(dateText,dateTimeFormatter).atStartOfDay();
            }
            return new FetchedResource(
                    externalId,
                    title,
                    content,
                    url,
                    publishedAt
            );

        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
