package Client.Gui;

import Client.Controller.NetworkChatManager;
import Client.Model.Chat;
import Client.Model.Message;
import Client.Model.User;
import Client.Security.ClientProtocols;
import Client.Security.ClientSecurity;
import Client.Security.WebSocketClientConnection;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URI;
import java.util.List;

public class GuiClient extends Application {

    private WebSocketClientConnection connection;
    private NetworkChatManager manager;
    private User currentUser;
    private Stage primaryStage;
    private Timeline messagePoller; // Для автооновлення чату


    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Java WebSocket Messenger");

        try {
            // 1. Підключення (як у консольній версії)
            connection = new WebSocketClientConnection(new URI("ws://localhost:8080"));
            connection.connectBlocking();

            // Показуємо екран логіну
            showLoginScene();

        } catch (Exception e) {
            showAlert("Connection Error", "Could not connect to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- ЕКРАН 1: Логін / Реєстрація ---
    private void showLoginScene() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center;");

        Label label = new Label("Вітаємо! Увійдіть або зареєструйтесь");
        TextField loginField = new TextField();
        loginField.setPromptText("Логін");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Пароль");

        Button btnLogin = new Button("Увійти (Auth)");
        Button btnRegister = new Button("Реєстрація");

        ClientProtocols protocols = new ClientProtocols(connection, new ClientSecurity());


        // Логіка входу
        btnLogin.setOnAction(e -> {
            boolean authSuccess = protocols.authentication(loginField.getText(),passField.getText());

            System.out.println("Sending auth request for: " + loginField.getText());
            if (authSuccess) {
                this.currentUser = new User();
                // Важливо: зазвичай User заповнюється даними з сервера,
                // тут ми просто створюємо нового, як у вашому main.
                this.currentUser.setNickName(loginField.getText()); // Припустимо сеттер

                this.manager = new NetworkChatManager(connection, currentUser);
                showChatListScene();
            } else {
                showAlert("Помилка", "Невірний логін або пароль");
                label.setText("Неправильні дані спробуйте ще раз");
            }
        });

        btnRegister.setOnAction(e -> {
            boolean regResult = protocols.registration(loginField.getText(),passField.getText());
            if(regResult){
                label.setText("Реєстрація успішна");
            }else{
                label.setText("Помилка реєстрації");
            }

        });

        root.getChildren().addAll(label, loginField, passField, btnLogin, btnRegister);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    // --- ЕКРАН 2: Список чатів ---
    private void showChatListScene() {
        if (messagePoller != null) messagePoller.stop(); // Зупинити оновлення повідомлень, якщо воно йшло

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label header = new Label("Мої чати");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<Chat> chatListView = new ListView<>();

        // Кнопка оновити список чатів
        Button btnRefresh = new Button("Оновити список");
        btnRefresh.setOnAction(e -> refreshChatList(chatListView));

        // Кнопка створити новий чат
        HBox createBox = new HBox(5);
        TextField newChatUser = new TextField();
        newChatUser.setPromptText("Логін користувача");
        Button btnCreate = new Button("Створити чат");
        btnCreate.setOnAction(e -> {
            if (!newChatUser.getText().isEmpty()) {
                manager.createChat(newChatUser.getText());
                try { Thread.sleep(200); } catch (Exception ex){} // чекаємо відповіді сервера
                refreshChatList(chatListView);
                newChatUser.clear();
            }
        });
        createBox.getChildren().addAll(newChatUser, btnCreate);

        // Початкове завантаження
        refreshChatList(chatListView);

        // Обробка кліку по чату
        chatListView.setOnMouseClicked(e -> {
            Chat selected = chatListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showChatSessionScene(selected);
            }
        });

        // Налаштування вигляду комірки списку
        chatListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Chat item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " [" + item.getType() + "]");
                }
            }
        });

        root.getChildren().addAll(header, createBox, btnRefresh, chatListView);
        primaryStage.setScene(new Scene(root, 400, 500));
    }

    private void refreshChatList(ListView<Chat> listView) {
        List<Chat> chats = manager.fetchChats();
        ObservableList<Chat> observableChats = FXCollections.observableArrayList(chats);
        listView.setItems(observableChats);
    }

    // --- ЕКРАН 3: Чат (листування) ---
    private void showChatSessionScene(Chat chat) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        HBox topBar = new HBox(10);
        Button btnBack = new Button("<- Назад");
        btnBack.setOnAction(e -> showChatListScene());
        Label chatName = new Label("Чат: " + chat.getName());
        chatName.setStyle("-fx-font-weight: bold;");
        topBar.getChildren().addAll(btnBack, chatName);

        // Область повідомлень
        ListView<String> messagesView = new ListView<>();

        // Область вводу
        HBox inputBox = new HBox(5);
        TextField msgInput = new TextField();
        HBox.setHgrow(msgInput, Priority.ALWAYS);
        Button btnSend = new Button("Надіслати");

        // Логіка відправки
        btnSend.setOnAction(e -> {
            String text = msgInput.getText();
            if (!text.isEmpty()) {
                // Створюємо об'єкт повідомлення локально (або manager сам створить)
                // У вашому коді manager.writeMessage використовує Scanner.
                // Нам треба викликати manager.sendMessage напряму.

                Message msg = new Message();
                msg.setText(text);
                // Sender встановлюється на сервері або в менеджері,
                // але для локального об'єкта можемо вказати:
                msg.setSender(currentUser);

                manager.sendMessage(msg, chat);
                msgInput.clear();

                // Одразу оновити
                updateMessages(chat, messagesView);
            }
        });

        inputBox.getChildren().addAll(msgInput, btnSend);
        root.getChildren().addAll(topBar, messagesView, inputBox);
        VBox.setVgrow(messagesView, Priority.ALWAYS); // Список на всю висоту

        // --- АВТОМАТИЧНЕ ОНОВЛЕННЯ (Polling) ---
        // Створюємо таймер, який кожну секунду оновлює історію
        messagePoller = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            updateMessages(chat, messagesView);
        }));
        messagePoller.setCycleCount(Timeline.INDEFINITE);
        messagePoller.play();

        // Перше завантаження
        updateMessages(chat, messagesView);

        primaryStage.setScene(new Scene(root, 500, 600));
    }

    private void updateMessages(Chat chat, ListView<String> view) {
        // Логіка оновлення з MainClient
        manager.updateChatHistory(chat, 0);

        ObservableList<String> items = FXCollections.observableArrayList();
        if (chat.getMessages() != null) {
            for (Message m : chat.getMessages()) {
                String time = (m.getSentTime() != null) ? m.getSentTime().toLocalTime().toString() : "..";
                String sender = (m.getSender() != null) ? m.getSender().getNickName() : "Unknown";
                items.add(String.format("[%s] %s: %s", time, sender, m.getText()));
            }
        }

        // Оновлюємо список тільки якщо щось змінилось (проста перевірка)
        if (view.getItems().size() != items.size()) {
            view.setItems(items);
            view.scrollTo(items.size() - 1); // Прокрутка вниз
        }
    }

    // Хелпер для діалогів
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Заглушка для адаптації ClientProtocols
    private boolean performAuthHack(ClientProtocols p, String l, String pass) {
        // УВАГА: Оскільки я не бачу код ClientProtocols, я не знаю, як туди передати дані.
        // Вам потрібно в класі ClientProtocols додати метод:
        // public boolean authentication(String login, String password) { ... }
        // який робить те саме, що і версія зі Scanner, але бере дані з аргументів.

        // Тимчасово повертаємо true, щоб GUI працював
        // return p.authentication(l, pass);
        return true;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (connection != null) {
            connection.close(); // Закриваємо сокет при виході
        }
    }
}