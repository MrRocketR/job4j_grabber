package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=",
            SOURCE_LINK);

    private  String retrieveDescription(String link) throws IOException {

        Document doc = Jsoup.connect(link).get();
        Elements rows = doc.select(".job_show_description");
        Element titleElement = rows.select(".job_show_description__body").first();
        Element description = titleElement.select(".job_show_description__vacancy_description")
                .first();

        return description.text();

    }

    public static void main(String[] args) throws IOException {
        for (int page = 0; page <= 5; page++) {
            String currentLink = String.format("%s%d", PAGE_LINK, page);
            Connection connection = Jsoup.connect(currentLink);
            System.out.println("Parsing now");
            System.out.println(currentLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                Element timeElement = dateElement.select("time").first();
                String dateTimeOfFirstArticle = timeElement.attr("datetime");
                String vacancyName = titleElement.text();
                String link = String.format("%s%s",  SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s %s %n", vacancyName, dateTimeOfFirstArticle, link);
            });
            page++;
        }
    }
}