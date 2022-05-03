package team.proiectextins.repository.db;

import team.proiectextins.domain.Friendship;
import team.proiectextins.domain.Tuple;
import team.proiectextins.domain.validators.Validator;
import team.proiectextins.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {

    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;

    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("id nu poate fi null");
        String sql = "select * from friendships where id1 = ? and id2 = ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id.getLeft());
            ps.setLong(2, id.getRight());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
//                String stringDate = resultSet.getString("friendship_date");
//                LocalDate frDateTime = LocalDate.parse(stringDate);
                LocalDate frDateTime = (resultSet.getDate("friendship_date")).toLocalDate();


                Tuple tuple = new Tuple(id1, id2);
                Friendship friendship = new Friendship();
                friendship.setId(tuple);
                friendship.setFriendshipDate(frDateTime);

                return friendship;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship findOne(String string) {
        return null;
    }

    @Override
    public List<Friendship> findMatching(Long currentUser, Long friend) {
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
//                String stringDate = resultSet.getString("friendship_date");
//                LocalDate frDate = LocalDate.parse(stringDate);
                LocalDate frDate = (resultSet.getDate("friendship_date")).toLocalDate();


                Friendship friendship = new Friendship();
                //Tuple tuple = new Tuple(id1,id2); //asa imi da Raw use of Tuple
                Tuple<Long, Long> tuple = new Tuple<>(id1, id2);
                friendship.setId(tuple);
                friendship.setFriendshipDate(frDate);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi null!");
        validator.validate(entity);

        String sql = "insert into friendships (id1, id2, friendship_date) values (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            LocalDate fdate = entity.getFriendshipDate();

            ps.setDate(3, Date.valueOf(LocalDate.now()));
            //ps.setString(3,entity.getFriendshipDate().toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship delete(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("ID nu poate fi null!");
        String sql = "delete from friendships where id1 = ? and id2 = ?";
        int row_count = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id.getLeft());
            ps.setLong(2, id.getRight());
            Friendship removed = findOne(id);
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
    public Friendship update(Friendship entity) {
        return null;
    }
}

