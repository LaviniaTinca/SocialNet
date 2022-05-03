package team.proiectextins.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import team.proiectextins.StartApplication;
import team.proiectextins.domain.FriendRequest;
import team.proiectextins.domain.FriendRequestDTO;
import team.proiectextins.domain.Status;
import team.proiectextins.domain.User;
import team.proiectextins.service.FriendRequestService;
import team.proiectextins.service.MessageService;
import team.proiectextins.service.Service;
import team.proiectextins.utils.events.FriendRequestChangeEvent;
import team.proiectextins.utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController extends UserController implements Observer<FriendRequestChangeEvent> {
    ObservableList<FriendRequestDTO> model = FXCollections.observableArrayList();
    ObservableList<FriendRequestDTO> model2 = FXCollections.observableArrayList();

    @FXML
    TableView<FriendRequestDTO> tableView;
    @FXML
    TableView<FriendRequestDTO> sentRequestsTableView;
    @FXML
    TableColumn<FriendRequest, String> fromTableColumn;

    @FXML
    TableColumn<FriendRequest, String> statusTableColumn;
    @FXML
    TableColumn<FriendRequest, String> dateTableColumn;
    @FXML
    TableColumn<FriendRequest, String> toColumn;


    /**
     * seteaza service-ul
     *
     * @param service   de tip Service
     * @param frService obiect de tipul  FriendRequestService
     * @param user      User
     */
    public void setService(Service service, FriendRequestService frService, MessageService msgService, User user) {
        friendRequestService = frService;
        this.service = service;
        this.msgService = msgService;
        friendRequestService.addObserver(this);
        loggedInAs(user.getUsername());
        initModel();
        initModelSentRequest();
    }


    /**
     * populeaza tabelul din ui cu date
     */
    @FXML
    public void initialize() {
        fromTableColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableView.setItems(model);
        sentRequestsTableView.setItems(model2);
    }

    /**
     * populeaza lista observator cu date - pt cererile primite
     */
    protected void initModel() {
        Iterable<FriendRequestDTO> requests = friendRequestService.getFriendRequests1(currentUser.getId());
        List<FriendRequestDTO> list = StreamSupport.stream(requests.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(list);
    }

    private void initModelSentRequest() {
        Iterable<FriendRequestDTO> requests = friendRequestService.getSentFriendRequests1(currentUser.getId());
        List<FriendRequestDTO> list = StreamSupport.stream(requests.spliterator(), false)
                .collect(Collectors.toList());
        model2.setAll(list);
    }


    @Override
    public void update(FriendRequestChangeEvent friendRequestEvent) {
        initModel();
        initModelSentRequest();
    }

    /**
     * accepta o cerere de prietenie la actionarea butonului
     *
     * @param event actiune
     */
    public void acceptFriendRequestButtonOnAction(ActionEvent event) {

        FriendRequestDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            friendRequestService.manageFriendRequest(selected.getId().getLeft(), selected.getId().getRight(), Status.APPROVED);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Tu si " + selected.getFrom() + " sunteti de acum prieteni!", "Ai acceptat cererea de prietenie!");
            //MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Congratulation! "+selected.getFrom()+ " is your friend now!", "You have accepted the friend request ");
        } else {
            MessageAlert.showErrorMessage(null, "Nu ai selectat nici o inregistrare!");
        }


    }

    /**
     * respinge o cerere de prietenie la actionarea butonului corespunzator
     *
     * @param event de tip ActionEvent
     */
    public void rejectFriendRequestButtonOnAction(ActionEvent event) {

        FriendRequestDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            friendRequestService.manageFriendRequest(selected.getId().getLeft(), selected.getId().getRight(), Status.REJECTED);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "La revedere " + selected.getFrom() + " !", "Ai sters cererea de prietenie!");
            //MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Good bye "+selected.getFrom()+ " !", "You have rejected the friend request ");
        } else {
            MessageAlert.showErrorMessage(null, "Nu ai selectat nici o inregistrare!");
        }

    }

    public void deleteRequestButtonOnAction(ActionEvent event) {
        FriendRequestDTO selected = sentRequestsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            friendRequestService.deleteFriendRequest(selected.getId().getLeft(), selected.getId().getRight());
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "La revedere " + selected.getTo() + " !", "Ai sters cererea de prietenie trimisa!");
            //MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Good bye "+selected.getFrom()+ " !", "You have rejected the friend request ");
        } else {
            MessageAlert.showErrorMessage(null, "Nu ai selectat nici o inregistrare!");
        }

    }
}
