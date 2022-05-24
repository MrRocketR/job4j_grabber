package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.model.Post;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String link) {
        String rsl = null;
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Element descriptionElement = document.selectFirst(".style-ugc");
            rsl = descriptionElement.text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> listOfPosts = new ArrayList<>();
        HarbCareerDateTimeParser parser = new HarbCareerDateTimeParser();
        Connection connection = Jsoup.connect(link);
        try {
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                Element timeElement = dateElement.select("time").first();
                String vacancyDate = timeElement.attr("datetime");
                String vacancyName = titleElement.text();
                String vacancyLink = linkElement.attr("href");
                String vacancyDescription = retrieveDescription(vacancyLink);
                listOfPosts.add(new Post(vacancyName, vacancyLink,
                        vacancyDescription, parser.parse(vacancyDate)
                ));
            });
        } catch (Exception se) {
            se.printStackTrace();
        }
        return listOfPosts;
    }
}















