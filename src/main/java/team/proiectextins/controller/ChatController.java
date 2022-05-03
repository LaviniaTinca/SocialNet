package team.proiectextins.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import team.proiectextins.domain.*;
import team.proiectextins.domain.validators.exceptions.ValidationException;
import team.proiectextins.service.FriendRequestService;
import team.proiectextins.service.MessageService;
import team.proiectextins.service.Service;
import team.proiectextins.utils.events.MessageChangeEvent;
import team.proiectextins.utils.events.MessageEventType;
import team.proiectextins.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class ChatController extends UserController implements Observer<MessageChangeEvent> {
    ObservableList<MessageDTO> chatModel = FXCollections.observableArrayList();
    Long friendID;

    @FXML
    TableView<MessageDTO> chatTableView;
    @FXML
    TableColumn<Message, String> fromColumn;
    @FXML
    TableColumn<Message, String> messageTableColumn;
    @FXML
    TableColumn<Message, String> timeStampTableColumn;
    @FXML
    TableColumn<Message, String> replyTo;
    @FXML
    private TextField messageField;

    public void setService(Service service, FriendRequestService frService, MessageService msgService, User user, Long friendID) {
        friendRequestService = frService;
        this.service = service;
        this.friendID = friendID;
        this.msgService = msgService;
        this.currentUser = user;
        msgService.addObserver(this);
        loggedInAs(user.getUsername());
        initModel();
        pollingMessages();
    }

    protected void initModel() {
        List<MessageDTO> msgs = msgService.getChat(currentUser.getId(), friendID);
        chatModel.setAll(msgs);
        chatTableView.scrollTo(chatModel.size() - 1);
    }

    @FXML
    public void initialize() {
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        messageTableColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        timeStampTableColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        replyTo.setCellValueFactory(new PropertyValueFactory<>("reply"));
        chatTableView.setItems(chatModel);
    }

    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        initModel();
    }

    /**
     * sterge o prietenie la actionarea butonului corespunzator
     */

    public void sendMessage(ActionEvent actionEvent) {
        ArrayList<Long> recipients = new ArrayList<>();
        recipients.add(friendID);
        try {
            msgService.sendMessage(currentUser.getId(), recipients, messageField.getText());
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        messageField.clear();
    }

    public void onEnter(ActionEvent ae) {
        sendMessage(ae);
    }

    public void replyMessage(ActionEvent actionEvent) {
        MessageDTO selected = chatTableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            MessageAlert.showErrorMessage(null, "Trebuie sa selectezi un mesaj!");
        } else {
            try {
                msgService.sendReply(currentUser.getId(), selected.getId(), messageField.getText());
            } catch (ValidationException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }

        messageField.clear();
    }

    private void pollingMessages() {
        Timeline fiveSecondsWonder = new Timeline(
            new KeyFrame(Duration.seconds(8), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    update(new MessageChangeEvent(MessageEventType.ADD, null));
                }
            })
        );
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();
    }
}
