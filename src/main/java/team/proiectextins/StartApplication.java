package team.proiectextins;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import team.proiectextins.controller.LoginController;
import team.proiectextins.domain.*;
import team.proiectextins.domain.validators.FriendRequestValidator;
import team.proiectextins.domain.validators.FriendshipValidator;
import team.proiectextins.domain.validators.MessageValidator;
import team.proiectextins.domain.validators.UserValidator;
import team.proiectextins.repository.Repository;
import team.proiectextins.repository.db.FriendRequestDbRepository;
import team.proiectextins.repository.db.FriendshipDbRepository;
import team.proiectextins.repository.db.MessageDbRepository;
import team.proiectextins.repository.db.UserDbRepository;
import team.proiectextins.service.FriendRequestService;
import team.proiectextins.service.MessageService;
import team.proiectextins.service.Service;


import java.io.IOException;

public class StartApplication extends Application {
    Repository<Long, User> repoDbUser;
    Repository<Tuple<Long, Long>, Friendship> repoDbFriendship;
    Repository<Tuple<Long, Long>, FriendRequest> repoDbFriendRequest;
    Repository<Long, Message> repoDbMsg;
    Service service;
    MessageService messageService;
    FriendRequestService friendRequestService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        repoDbUser = new UserDbRepository(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new UserValidator()
        );
        repoDbFriendship = new FriendshipDbRepository(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new FriendshipValidator()
        );
        repoDbFriendRequest = new FriendRequestDbRepository(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new FriendRequestValidator()
        );
        repoDbMsg = new MessageDbRepository(
                "jdbc:postgresql://localhost:5432/SocialNetwork",
                "postgres",
                "postgres",
                new MessageValidator()
        );

        service = new Service(repoDbUser, repoDbFriendship);
        friendRequestService = new FriendRequestService(repoDbUser, repoDbFriendship, repoDbFriendRequest);
        messageService = new MessageService(repoDbUser, repoDbMsg, repoDbFriendship);
        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loginLoader.load(), 620, 420);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(620);
        primaryStage.setTitle("Cappuccino Social Network");
        primaryStage.getIcons().add(new Image("https://cdn2.iconfinder.com/data/icons/metro-ui-dock/512/Java.png"));

        LoginController loginController = loginLoader.getController();
        loginController.setService(service, friendRequestService, messageService);
    }
}

