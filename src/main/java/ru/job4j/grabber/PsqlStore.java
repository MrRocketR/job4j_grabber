package ru.job4j.grabber;

import ru.job4j.model.Post;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        String sql = "INSERT INTO post (name, text, link, created) "
                + "values (?,?,?,?)  \n "
                + "ON CONFLICT (link) \n"
                + "DO NOTHING;";
        try (PreparedStatement pS = cnn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pS.setString(1, post.getTitle());
            pS.setString(2, post.getDescription());
            pS.setString(3, post.getLink());
            pS.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            pS.execute();
            ResultSet generatedKeys = pS.getGeneratedKeys();
            if (generatedKeys.next()) {
                post.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException SQLEx) {
            SQLEx.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        String sql = "select * from post";
        try (PreparedStatement statement = cnn.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(getFromDB(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    private Post getFromDB(ResultSet set) throws SQLException {
        return new Post(
                set.getInt("id"),
                set.getString("name"),
                set.getString("text"),
                set.getString("link"),
                set.getTimestamp("created").toLocalDateTime()
        );
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        String sql = "select * from post where id = (?)";
        try (PreparedStatement statement = cnn.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = getFromDB(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Properties config = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("app.properties");) {
            config.load(in);
        }
        try (PsqlStore psqlStore = new PsqlStore(config);) {
            Post post1 = new Post("name1", "link1", "desc1", LocalDateTime.now());
            Post post2 = new Post("name2", "link2", "desc2", LocalDateTime.now());
            Post post3 = new Post("name3", "link3", "desc3", LocalDateTime.now());
            psqlStore.save(post1);
            psqlStore.save(post2);
            psqlStore.save(post3);
            List<Post> posts = psqlStore.getAll();
            System.out.println(posts);
            Post find = psqlStore.findById(post1.getId());
            System.out.println(find);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        }
    }
