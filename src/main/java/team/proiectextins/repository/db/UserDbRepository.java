package team.proiectextins.repository.db;

import team.proiectextins.domain.User;
import team.proiectextins.domain.validators.Validator;
import team.proiectextins.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDbRepository implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;

    public UserDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    public User findOne(String userName) {
        //atentie poate confunda userName cu username-ul de conectare la baza de date
        if (userName == null) {
            throw new IllegalArgumentException("username nu poate fi null");
        }

        String sql = "select * from users where username = ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String uName = resultSet.getString("username");
                String password = resultSet.getString("password");
                User user = new User(firstName, lastName, uName, password);
                user.setId(id);
                return user;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> findMatching(Long currentUser, Long friend) {
        return null;
    }

    @Override
    public User findOne(Long idUser) {
        if (idUser == null)
            throw new IllegalArgumentException("id nu poate fi null");
        String sql = "select * from users where id = ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, idUser);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("username");
                String userPassword = resultSet.getString("password");
                User user = new User(firstName, lastName, userName, userPassword);
                user.setId(id);
                return user;
            }

        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User user = new User(firstName, lastName);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return users;
    }

    @Override
    public User save(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");
        validator.validate(entity);

        String sql = "insert into users (first_name, last_name, username, password ) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getUsername());
            ps.setString(4, entity.getPassword());

            ps.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public User delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID nu poate fi null!");
        }

        String sql = "delete from users where id = ?";
        int row_count = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            User removed = findOne(id);
            row_count = ps.executeUpdate();
            if (row_count > 0)
                return removed;

        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public User update(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi null!");
        }

        validator.validate(entity);
        String sql = "update users set first_name = ?, last_name = ? where id = ?";
        int row_count = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setLong(3, entity.getId());

            row_count = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (row_count > 0) {
            return null;
        }

        return entity;
    }
}

