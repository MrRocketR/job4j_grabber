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

    public static final int PAGE = 5;

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

    private Post poster(Element element) {
        Element titleElement = element.select(".vacancy-card__title").first();
        Element dateElement = element.select(".vacancy-card__date").first();
        Element linkElement = titleElement.child(0);
        Element timeElement = dateElement.select("time").first();
        String vacancyDate = timeElement.attr("datetime");
        String vacancyName = titleElement.text();
        final String LINK = "https://career.habr.com";
        String vacancyLink  = String.format("%s%s", LINK, linkElement.attr("href"));
        String vacancyDescription = retrieveDescription(vacancyLink);
        return new Post(vacancyName, vacancyDescription, vacancyLink,
                dateTimeParser.parse(vacancyDate));
    }

    @Override
    public List<Post> list(String link) {
        List<Post> listOfPosts = new ArrayList<>();
        for (int i = 1; i <= PAGE; i++) {
            try {
                String currentLink = String.format("%s%d", link, i);
                Connection connection = Jsoup.connect(currentLink);
                System.out.println("Parsing now");
                System.out.println(currentLink);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    listOfPosts.add(poster(row));
                });
            } catch (Exception se) {
                se.printStackTrace();
            }
        }
        return listOfPosts;
    }
}
















