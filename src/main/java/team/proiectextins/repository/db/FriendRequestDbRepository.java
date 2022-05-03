package team.proiectextins.repository.db;

import team.proiectextins.domain.FriendRequest;
import team.proiectextins.domain.Status;
import team.proiectextins.domain.Tuple;
import team.proiectextins.domain.validators.Validator;
import team.proiectextins.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDbRepository implements Repository<Tuple<Long, Long>, FriendRequest> {
    private String url;
    private String username;
    private String password;
    private Validator<FriendRequest> validator;

    public FriendRequestDbRepository(String url, String username, String password, Validator<FriendRequest> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public FriendRequest findOne(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("id nu poate fi null");
        String sql = "select * from friendrequests where from_user = ? and to_user = ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id.getLeft());
            ps.setLong(2, id.getRight());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("from_user");
                Long id2 = resultSet.getLong("to_user");
                LocalDate fDate = (resultSet.getDate("friendrequest_date")).toLocalDate();
                String s = resultSet.getString("status");


                Tuple tuple = new Tuple(id1, id2);
                FriendRequest friendRequest = new FriendRequest();
                friendRequest.setId(tuple);
                friendRequest.setStatus(Status.valueOf(s));
                friendRequest.setDate(fDate);

                return friendRequest;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
        return null;
    }

    @Override
    public FriendRequest findOne(String string) {
        return null;
    }

    @Override
    public List<FriendRequest> findMatching(Long currentUser, Long friend) {
        return null;
    }

    @Override
    public Iterable<FriendRequest> findAll() {

        List<FriendRequest> friendRequestList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendrequests");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("from_user");
                Long id2 = resultSet.getLong("to_user");
                LocalDate fDate = (resultSet.getDate("friendrequest_date")).toLocalDate();
                String s = resultSet.getString("status");
                Tuple<Long, Long> tuple = new Tuple<>(id1, id2);
                FriendRequest friendRequest = new FriendRequest();
                friendRequest.setId(tuple);
                friendRequest.setStatus(Status.valueOf(s));
                friendRequest.setDate(fDate);
                friendRequestList.add(friendRequest);
            }
            return friendRequestList;
        } catch (SQLException e) {
            e.printStackTrace();
            //System.out.println(e.getMessage());
        }
        return friendRequestList;
    }


    @Override
    public FriendRequest save(FriendRequest entity) {

        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi null!");
        validator.validate(entity);

        String sql = "insert into friendrequests (from_user, to_user, status, friendrequest_date) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            ps.setString(3, entity.getStatus().toString());
            ps.setDate(4, Date.valueOf(LocalDate.now()));

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public FriendRequest delete(Tuple<Long, Long> id) {

        if (id == null)
            throw new IllegalArgumentException("ID nu poate fi null!");
        String sql = "delete from friendrequests where from_user = ? and to_user = ?";
        int row_count = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id.getLeft());
            ps.setLong(2, id.getRight());
            FriendRequest removed = findOne(id);
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
    public FriendRequest update(FriendRequest entity) {

        if (entity == null)
            throw new IllegalArgumentException("Entitatea nu poate fi null!");
        validator.validate(entity);
        String sql = "update friendrequests set status = ?, friendrequest_date=? where from_user = ? and to_user =?";
        int row_count = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getStatus().toString());
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setLong(3, entity.getId().getLeft());
            ps.setLong(4, entity.getId().getRight());
            row_count = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (row_count > 0)
            return null;
        return entity;
    }
}

