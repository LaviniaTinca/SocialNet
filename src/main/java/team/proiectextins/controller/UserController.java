package team.proiectextins.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import team.proiectextins.StartApplication;
import team.proiectextins.domain.User;
import team.proiectextins.domain.validators.exceptions.FriendshipException;
import team.proiectextins.service.FriendRequestService;
import team.proiectextins.service.MessageService;
import team.proiectextins.service.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController {
    protected User currentUser;
    protected Service service;
    protected FriendRequestService friendRequestService;
    protected MessageService msgService;
    ObservableList<User> model = FXCollections.observableArrayList();


    public UserController() {
    }

    @FXML
    private Label loggedUserNameLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private TextField searchTextField;
    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> firstNameTableColumn;
    @FXML
    TableColumn<User, String> lastNameTableColumn;

    /**
     * seteaza service-ul
     *
     * @param service              Service
     * @param friendRequestService FriendRequestService
     */
    public void setService(Service service, FriendRequestService friendRequestService, MessageService msgService) {
        this.service = service;
        this.friendRequestService = friendRequestService;
        this.msgService = msgService;
        initModel1();
    }

    /**
     * initializeaza tabelul din ui cu date
     */
    @FXML
    public void initialize() {
        //idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
        searchTextField.textProperty().addListener(o -> handleFilter());
    }

    /**
     * populeaza lista observator cu date
     */
    protected void initModel1() {
        Iterable<User> requests = service.getAllUsers();
        List<User> list = StreamSupport.stream(requests.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(list);
    }

    /**
     * gestioneaza search-ul dupa un utilizator
     */
    protected void handleFilter() {

        Iterable<User> requests = service.getAllUsersFilterStream(searchTextField.getText());
        List<User> list = StreamSupport.stream(requests.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(list);
    }

    /**
     * retine utilizatorul logat
     *
     * @param username String
     */
    public void loggedInAs(String username) {
        User user = service.getUserByUsername(username);
        this.currentUser = user;
        loggedUserNameLabel.setText(user.getFirstName() + " " + user.getLastName());
    }

    /**
     * delogare
     *
     * @param e actiune
     * @throws IOException Exceptie
     */
    public void logoutButtonOnAction(ActionEvent e) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(StartApplication.class.getResource("login.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 620, 420);

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

        LoginController loginController = newMenu.getController();
        loginController.setService(service, friendRequestService, msgService);

        stage.setScene(newMenuScene);
        stage.show();
    }

    /**
     * trimite o cerere de prietenie la actionarea butonului corespunzator
     *
     * @param event actiune
     */
    public void sendFriendRequestButtonOnAction(ActionEvent event) {
        messageLabel.setVisible(false);
        messageLabel.setText("");
        User selected = tableView.getSelectionModel().getSelectedItem();
        try {
            if (selected != null) {
                friendRequestService.sendFriendRequest(currentUser.getId(), selected.getId());
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Cerere de prietenie trimisa catre: " + selected.getFirstName() + " " + selected.getLastName(), "Ai trimis o cerere de prietenie");
            } else {
                MessageAlert.showErrorMessage(null, "Nu ai selectat nici o inregistrare!");
            }
        } catch (FriendshipException e) {
            messageLabel.setVisible(true);
            messageLabel.setText(e.getMessage());
        }
    }


    public void friendRequestsButtonOnAction(ActionEvent e) throws IOException {
        switchSceneToFriendRequests(e);
    }

    /**
     * schimba scena la actionarea butonului FriendRequest
     *
     * @param event actiune
     * @throws IOException exceptie
     */
    protected void switchSceneToFriendRequests(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(StartApplication.class.getResource("friendRequests-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 800, 420);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //aici am incercat cateva variante de MenuItem:
        //Stage owner = (Stage)friendRequestsMenuItem.getParentPopup().getOwnerWindow();
        //Scene scene = owner.getScene();
        //Stage stage = (Stage) ((MenuItem)event.getTarget()).getParentPopup().getScene().getWindow();

        stage.setScene(newMenuScene);
        stage.show();
        FriendRequestsController controller = newMenu.getController();
        controller.setService(service, friendRequestService, msgService, currentUser);
        controller.loggedInAs(currentUser.getUsername());
        //controller.initialize2();
    }

    public void friendsButtonOnAction(ActionEvent e) throws IOException {
        switchSceneToFriendship(e);
    }

    /**
     * schimba scena la actionarea butonului Friends
     *
     * @param event Actiune
     * @throws IOException e
     */
    protected void switchSceneToFriendship(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(StartApplication.class.getResource("friends-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 800, 420);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(newMenuScene);
        stage.show();
        FriendsController controller = newMenu.getController();
        controller.setService(service, friendRequestService, msgService, currentUser);
        controller.loggedInAs(currentUser.getUsername());
    }

    public void homeButtonOnAction(ActionEvent e) throws IOException {
        switchSceneToUser(e);
    }

    /**
     * schimba scena la actionarea butonului Home
     *
     * @param event actiune
     * @throws IOException exceptie
     */
    protected void switchSceneToUser(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(StartApplication.class.getResource("user-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 800, 420);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        UserController userController = newMenu.getController();
        userController.setService(service, friendRequestService, msgService);
        userController.loggedInAs(currentUser.getUsername());

        stage.setScene(newMenuScene);
        stage.show();
    }

}
