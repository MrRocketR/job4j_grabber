package ru.job4j.grabber;

import ru.job4j.model.Post;
import java.sql.*;
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
}
