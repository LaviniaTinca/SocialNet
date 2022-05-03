package team.proiectextins.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import team.proiectextins.StartApplication;
import team.proiectextins.domain.*;
import team.proiectextins.service.FriendRequestService;
import team.proiectextins.service.MessageService;
import team.proiectextins.service.Service;
import team.proiectextins.utils.events.FriendshipChangeEvent;
import team.proiectextins.utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendsController extends UserController implements Observer<FriendshipChangeEvent> {
    ObservableList<FriendshipDTO> model = FXCollections.observableArrayList();

    @FXML
    TableView<FriendshipDTO> tableView;
    @FXML
    TableColumn<Friendship, String> firstNameTableColumn;
    @FXML
    TableColumn<Friendship, String> lastNameTableColumn;
    @FXML
    TableColumn<FriendRequest, String> dateTableColumn;

    public void setService(Service service, FriendRequestService frService, MessageService msgService, User user) {
        friendRequestService = frService;
        this.service = service;
        this.msgService = msgService;
        service.addObserver(this);
        loggedInAs(user.getUsername());
        initModel();
    }

    @FXML
    public void initialize() {
        //idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableView.setItems(model);
    }

    protected void initModel() {
        //Predicate p1 = service.predicateFriendsOfAUser(currentUser.getId());
        Iterable<FriendshipDTO> requests = service.getFriendsStream(currentUser.getId(), service.predicateFriendsOfAUser(currentUser.getId()));
        List<FriendshipDTO> list = StreamSupport.stream(requests.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(list);
    }


    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initModel();
    }

    /**
     * sterge o prietenie la actionarea butonului corespunzator
     */
    public void deleteFriendshipButtonOnAction() {
        FriendshipDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            service.removeFriendship(selected.getId().getLeft(), selected.getId().getRight());
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "La revedere " + selected.getFirstName() + " " + selected.getLastName() + " !", "Ai sters prietenia!");
            //MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, selected.getFirstName() + " " + selected.getLastName() + " is is not your friend anymore!", "You have deleted the friendship");
        } else {
            MessageAlert.showErrorMessage(null, "Nu ai selectat nici o inregistrare!");
        }
    }

    public void openMessageWindow(ActionEvent ae) throws IOException {
        FriendshipDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            FXMLLoader chatLoader = new FXMLLoader(StartApplication.class.getResource("chat.fxml"));
            Scene chatScene = new Scene(chatLoader.load(), 600, 420);
            Stage chatPopUp = new Stage();

            ChatController controller = chatLoader.getController();
            Long friendID = Objects.equals(currentUser.getId(), selected.getId().getRight()) ? selected.getId().getLeft() : selected.getId().getRight();
            controller.setService(service, friendRequestService, msgService, currentUser, friendID);
            controller.loggedInAs(currentUser.getUsername());

            chatPopUp.setScene(chatScene);
            chatPopUp.setResizable(false);
            chatPopUp.setTitle("Chat as " + currentUser.getUsername());
            chatPopUp.getIcons().add(new Image("https://cdn2.iconfinder.com/data/icons/social-media-and-payment/64/-59-512.png"));
            chatPopUp.show();

        } else {
            MessageAlert.showErrorMessage(null, "Nu ai selectat niciun prieten!");
        }
    }
}
