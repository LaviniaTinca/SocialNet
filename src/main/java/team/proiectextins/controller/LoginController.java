package team.proiectextins.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import team.proiectextins.StartApplication;
import team.proiectextins.domain.User;
import team.proiectextins.domain.validators.exceptions.DataException;
import team.proiectextins.service.FriendRequestService;
import team.proiectextins.service.MessageService;
import team.proiectextins.service.Service;
import team.proiectextins.utils.PasswordManager;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoginController {
    private Service loginService;
    private FriendRequestService friendRequestService;
    private MessageService msgService;
    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;

    /**
     * seteaza service-ul
     * @param service Service
     * @param friendRequestService FriendRequestService
     */
    public void setService(Service service, FriendRequestService friendRequestService, MessageService msgService) {
        this.loginService = service;
        this.friendRequestService = friendRequestService;
        this.msgService = msgService;
    }

    /**
     * inchide programul la actionarea butonului Cancel/Close
     * @param e actiune
     */
    public void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * logare
     * @param e actiune
     * @throws IOException exceptie
     */
    public void loginButtonOnAction(ActionEvent e) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        validateLogin(usernameTextField.getText());
        switchSceneToUser(e, usernameTextField.getText());
    }

    /**
     * valideaza datele din fereastra de logare
     * @param text String
     */
    public void validateLogin(String text) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User user = loginService.getUserByUsername(text);
        if (usernameTextField.getText().isBlank() && passwordPasswordField.getText().isBlank()) {
            loginMessageLabel.setText("Please enter username and password!");
            throw new DataException("Please enter username and password!");
        }
        if (user == null) {
            loginMessageLabel.setText("Invalid Username!");
            usernameTextField.clear();
            passwordPasswordField.clear();
            throw new DataException("Invalid username!");
        }

        // Verificam criptarea parolei raw
        if (!PasswordManager.validatePassword(passwordPasswordField.getText(), user.getPassword())) {
            loginMessageLabel.setText("Invalid password!");
            passwordPasswordField.clear();
            throw new DataException("Invalid password!");
        }
    }

    /**
     * schimba scena la actionarea butonului Home
     * @param event actiune
     * @param username String
     * @throws IOException exceptie
     */
    private void switchSceneToUser(ActionEvent event, String username) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(StartApplication.class.getResource("user-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 800, 420);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        UserController userController = newMenu.getController();
        userController.setService(loginService, friendRequestService, msgService);
        userController.loggedInAs(username);

        stage.setScene(newMenuScene);
        stage.show();
    }

    public void signUpButtonOnAction(ActionEvent event) throws IOException {
        //loginMessageLabel.setText("You try to sign UP");
        switchSceneToSignUp(event);

    }

    /**
     * schimba scena pt formularul de inregistrare
     * @param event actiune
     * @throws IOException exceptie
     */
    private void switchSceneToSignUp(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(StartApplication.class.getResource("signUp-view.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 620, 420);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        SignUpController signUpController = newMenu.getController();
        signUpController.setService(loginService, friendRequestService, msgService);

        stage.setScene(newMenuScene);
        stage.show();
    }


    public void onEnter(ActionEvent ae) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        loginButtonOnAction(ae);
    }
}
