package team.proiectextins.repository.db;

import team.proiectextins.domain.Message;
import team.proiectextins.domain.validators.Validator;
import team.proiectextins.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDbRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;

    public MessageDbRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Message findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id nu poate fi null");
        }

        String sql = "select * from messages where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Long from = resultSet.getLong("from_user");
                Long to = resultSet.getLong("to_user");
                Long reply = resultSet.getLong("reply");
                String text = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("timestamp").toLocalDateTime();

                Message message = new Message(from, to, text, date);
                message.setId(id);
                message.setReply(reply);

                return message;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        return null;
    }

    @Override
    public List<Message> findMatching(Long currentUser, Long friend) {
        List<Message> result = new ArrayList<>();
        String sql = "SELECT * from messages where (from_user = ? AND to_user = ?) OR (from_user = ? AND to_user = ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             ) {

            ps.setLong(1, currentUser);
            ps.setLong(2, friend);
            ps.setLong(3, friend);
            ps.setLong(4, currentUser);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from = resultSet.getLong("from_user");
                Long to = resultSet.getLong("to_user");
                Long reply = resultSet.getLong("reply");
                String text = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("timestamp").toLocalDateTime();

                Message message = new Message(from, to, text, date);
                message.setId(id);
                message.setReply(reply);
                result.add(message);
            }

            return result;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;

    }

    @Override
    public Message findOne(String string) {
        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from = resultSet.getLong("from_user");
                Long to = resultSet.getLong("to_user");
                Long reply = resultSet.getLong("reply");
                String text = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("timestamp").toLocalDateTime();

                Message message = new Message(from, to, text, date);
                message.setId(id);
                message.setReply(reply);
                messages.add(message);
            }

            return messages;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    @Override
    public Message save(Message msg) {
        if (msg == null) {
            throw new IllegalArgumentException("Mesajul nu poate fi null!");
        }
        validator.validate(msg);

        String sql = "insert into messages (from_user, to_user, message, timestamp, reply) values (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, msg.getFrom());
            ps.setLong(2, msg.getTo());
            ps.setString(3, msg.getText());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(5, msg.getReply());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Message delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID nu poate fi null!");
        }

        String sql = "delete from messages where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            Message removed = findOne(id);
            int updated = ps.executeUpdate();

            if (updated != 0) {
                return removed;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Message update(Message msg) {
        if (msg == null) {
            throw new IllegalArgumentException("Mesajul nu poate fi null!");
        }

        validator.validate(msg);
        String sql = "update messages set from_user = ?, to_user = ?, message = ?, timestamp = ? where id = ?";
        int row_count = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, msg.getFrom());
            ps.setLong(2, msg.getTo());
            ps.setString(3, msg.getText());
            ps.setTimestamp(4, Timestamp.valueOf(msg.getTimestamp()));
            row_count = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (row_count > 0) {
            return null;
        }
        return msg;
    }
}
