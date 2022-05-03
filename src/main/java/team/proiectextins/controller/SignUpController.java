package team.proiectextins.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.Objects;

public class SignUpController {
    private Service loginService;
    private FriendRequestService friendRequestService;
    private MessageService msgService;
    @FXML
    private Button cancelButton;
    @FXML
    private Button loginButton;
    @FXML
    private Button signUpButton;
    @FXML
    private Label signUpMessageLabel;
    @FXML
    private Label passwordMessageLabel;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    /**
     * seteaza service
     * @param service Service
     * @param friendRequestService FriendRequestService
     */
    public void setService(Service service, FriendRequestService friendRequestService, MessageService msgService) {
        this.loginService = service;
        this.friendRequestService = friendRequestService;
        this.msgService = msgService;
        firstNameTextField.requestFocus();
    }

    /**
     * inchide aplicatia la actionarea butonului Close/Cancel
     * @param e
     */
    public void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * schimba scena la logare
     * @param e actiune
     * @throws IOException exceptie
     */
    public void loginButtonOnAction(ActionEvent e) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(StartApplication.class.getResource("login.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 620, 420);
        Stage primaryStage = (Stage) loginButton.getScene().getWindow();

        LoginController loginController = newMenu.getController();
        loginController.setService(loginService, friendRequestService, msgService);

        primaryStage.setScene(newMenuScene);
        primaryStage.show();
    }

    /**
     * realizeaza inregistrarea formularului unui utilizator nou
     * @param e actiune
     * @throws IOException exceptie
     */
    public void signUpButtonOnAction(ActionEvent e) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        validateSignUp();
        // Saving to Database
        loginService.addUser(
            firstNameTextField.getText(),
            lastNameTextField.getText(),
            usernameTextField.getText(),
            PasswordManager.generateHash(passwordPasswordField.getText())
        );

        signUpMessageLabel.setText("User registered successfully!");

        confirmPasswordField.setVisible(false);
        signUpButton.setVisible(false);
    }

    /**
     * valideaza datele din formularul de inregistrare
     */
    private void validateSignUp() {
        User user = loginService.getUserByUsername(usernameTextField.getText());
        signUpMessageLabel.setText("");
        passwordMessageLabel.setText("");
        if (usernameTextField.getText().isBlank()
            || passwordPasswordField.getText().isBlank()
            || firstNameTextField.getText().isBlank()
            || lastNameTextField.getText().isBlank()
            || confirmPasswordField.getText().isBlank()) {
            signUpMessageLabel.setText("Please complete all the fields!");
            throw new DataException("Please complete all the fields!");
        }
        if (user != null) {
            signUpMessageLabel.setText("Username invalid");
            usernameTextField.setText("");
            passwordPasswordField.setText("");
            confirmPasswordField.setText("");
            throw new DataException("Invalid username!");
        }
        if (!Objects.equals(confirmPasswordField.getText(), passwordPasswordField.getText())) {
            passwordMessageLabel.setText("Passwords do not match!");
            passwordPasswordField.setText("");
            confirmPasswordField.setText("");
            throw new DataException("Invalid username!");
        }
    }
}
