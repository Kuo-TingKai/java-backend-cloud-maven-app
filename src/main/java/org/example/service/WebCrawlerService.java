package org.example.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "web.crawler.enabled", havingValue = "true", matchIfMissing = true)
public class WebCrawlerService {
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    public Document crawlPage(String url) throws IOException {
        if (restTemplate != null) {
            String html = restTemplate.getForObject(url, String.class);
            return Jsoup.parse(html, url);
        } else {
            return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get();
        }
    }
    
    public List<String> extractLinks(String url) throws IOException {
        Document doc = crawlPage(url);
        Elements links = doc.select("a[href]");
        List<String> linkList = new ArrayList<>();
        
        for (Element link : links) {
            String href = link.attr("abs:href");
            if (!href.isEmpty()) {
                linkList.add(href);
            }
        }
        
        return linkList;
    }
    
    public List<String> extractImages(String url) throws IOException {
        Document doc = crawlPage(url);
        Elements images = doc.select("img[src]");
        List<String> imageList = new ArrayList<>();
        
        for (Element img : images) {
            String src = img.attr("abs:src");
            if (!src.isEmpty()) {
                imageList.add(src);
            }
        }
        
        return imageList;
    }
    
    public Map<String, String> extractMetaData(String url) throws IOException {
        Document doc = crawlPage(url);
        Map<String, String> metaData = new HashMap<>();
        
        // Extract title
        Element title = doc.selectFirst("title");
        if (title != null) {
            metaData.put("title", title.text());
        }
        
        // Extract meta tags
        Elements metaTags = doc.select("meta");
        for (Element meta : metaTags) {
            String name = meta.attr("name");
            String property = meta.attr("property");
            String content = meta.attr("content");
            
            if (!name.isEmpty() && !content.isEmpty()) {
                metaData.put("meta:" + name, content);
            }
            if (!property.isEmpty() && !content.isEmpty()) {
                metaData.put("og:" + property, content);
            }
        }
        
        return metaData;
    }
    
    public String extractText(String url) throws IOException {
        Document doc = crawlPage(url);
        return doc.text();
    }
    
    public String extractTextBySelector(String url, String selector) throws IOException {
        Document doc = crawlPage(url);
        Elements elements = doc.select(selector);
        StringBuilder text = new StringBuilder();
        
        for (Element element : elements) {
            text.append(element.text()).append(" ");
        }
        
        return text.toString().trim();
    }
    
    public List<Map<String, String>> extractTableData(String url, String tableSelector) throws IOException {
        Document doc = crawlPage(url);
        Elements tables = doc.select(tableSelector);
        List<Map<String, String>> tableData = new ArrayList<>();
        
        for (Element table : tables) {
            Elements rows = table.select("tr");
            List<String> headers = new ArrayList<>();
            
            // Extract headers from first row
            if (!rows.isEmpty()) {
                Elements headerCells = rows.get(0).select("th, td");
                for (Element cell : headerCells) {
                    headers.add(cell.text().trim());
                }
            }
            
            // Extract data from remaining rows
            for (int i = 1; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cells = row.select("td");
                Map<String, String> rowData = new HashMap<>();
                
                for (int j = 0; j < Math.min(headers.size(), cells.size()); j++) {
                    rowData.put(headers.get(j), cells.get(j).text().trim());
                }
                
                if (!rowData.isEmpty()) {
                    tableData.add(rowData);
                }
            }
        }
        
        return tableData;
    }
    
    public Map<String, Object> crawlProductPage(String url) throws IOException {
        Document doc = crawlPage(url);
        Map<String, Object> productData = new HashMap<>();
        
        // Extract basic product information
        productData.put("url", url);
        productData.put("title", doc.selectFirst("title") != null ? doc.selectFirst("title").text() : "");
        
        // Extract price (common selectors)
        String[] priceSelectors = {
            ".price", ".product-price", "[data-price]", ".current-price", 
            ".sale-price", ".regular-price", ".price-current"
        };
        
        for (String selector : priceSelectors) {
            Element priceElement = doc.selectFirst(selector);
            if (priceElement != null) {
                productData.put("price", priceElement.text().trim());
                break;
            }
        }
        
        // Extract description
        String[] descSelectors = {
            ".description", ".product-description", ".product-summary", 
            ".product-details", "[data-description]"
        };
        
        for (String selector : descSelectors) {
            Element descElement = doc.selectFirst(selector);
            if (descElement != null) {
                productData.put("description", descElement.text().trim());
                break;
            }
        }
        
        // Extract images
        List<String> images = extractImages(url);
        productData.put("images", images);
        
        // Extract additional metadata
        Map<String, String> metaData = extractMetaData(url);
        productData.put("metadata", metaData);
        
        return productData;
    }
    
    public List<Map<String, Object>> crawlSearchResults(String searchUrl, String resultSelector) throws IOException {
        Document doc = crawlPage(searchUrl);
        Elements results = doc.select(resultSelector);
        List<Map<String, Object>> searchResults = new ArrayList<>();
        
        for (Element result : results) {
            Map<String, Object> resultData = new HashMap<>();
            
            // Extract title
            Element titleElement = result.selectFirst("h1, h2, h3, .title, .product-title");
            if (titleElement != null) {
                resultData.put("title", titleElement.text().trim());
            }
            
            // Extract link
            Element linkElement = result.selectFirst("a");
            if (linkElement != null) {
                resultData.put("link", linkElement.attr("abs:href"));
            }
            
            // Extract price
            Element priceElement = result.selectFirst(".price, .product-price");
            if (priceElement != null) {
                resultData.put("price", priceElement.text().trim());
            }
            
            // Extract image
            Element imageElement = result.selectFirst("img");
            if (imageElement != null) {
                resultData.put("image", imageElement.attr("abs:src"));
            }
            
            if (!resultData.isEmpty()) {
                searchResults.add(resultData);
            }
        }
        
        return searchResults;
    }
} 