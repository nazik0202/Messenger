package Client.Gui;

import Client.Controller.NetworkChatManager;
import Client.Model.Chat;
import Client.Model.Message;
import Client.Model.User;
import Client.Security.ClientProtocols;
import Client.Security.ClientSecurity;
import Client.Security.WebSocketClientConnection;

import Client.util.MessageStatus;
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
    private Timeline chatPoller;


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
// --- ЕКРАН 2: Список чатів ---
    private void showChatListScene() {
        if (messagePoller != null) messagePoller.stop();
        if (chatPoller != null) chatPoller.stop();

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label header = new Label("Мої чати (Сповіщення активні)");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<Chat> chatListView = new ListView<>();

        // Кнопка оновити список (ручна)
        Button btnRefresh = new Button("Оновити список");
        btnRefresh.setOnAction(e -> refreshChatListWithHistory(chatListView));

        // Створення чату
        HBox createBox = new HBox(5);
        TextField newChatUser = new TextField();
        newChatUser.setPromptText("Логін користувача");
        Button btnCreate = new Button("Створити чат");
        btnCreate.setOnAction(e -> {
            if (!newChatUser.getText().isEmpty()) {
                manager.createChat(newChatUser.getText());
                try { Thread.sleep(200); } catch (Exception ex){}
                refreshChatListWithHistory(chatListView);
                newChatUser.clear();
            }
        });
        createBox.getChildren().addAll(newChatUser, btnCreate);

        // --- ВІДОБРАЖЕННЯ (CellFactory) ---
        // Тут ми налаштовуємо вигляд рядка, щоб бачити останнє повідомлення
        chatListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Chat item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Формуємо текст: Назва чату
                    String displayText = item.getName();
                    String style = "";

                    // Перевіряємо, чи є повідомлення
                    if (item.getMessages() != null && !item.getMessages().isEmpty()) {
                        // Беремо останнє
                        Message lastMsg = item.getMessages().get(item.getMessages().size() - 1);
                        String preview = lastMsg.getText();
                        if (preview.length() > 15) preview = preview.substring(0, 15) + "...";

                        displayText += " : " + preview;

                        // Логіка СПОВІЩЕННЯ:
                        // Якщо останнє повідомлення НЕ від нас -> підсвічуємо
                        if (lastMsg.getSender() != null &&
                                !lastMsg.getSender().getNickName().equals(currentUser.getNickName()) &&
                                lastMsg.getStatus() != MessageStatus.READ) {
                            System.out.println(lastMsg.getStatus());
                            displayText += " (NEW!)";
                            style = "-fx-font-weight: bold; -fx-text-fill: #000080;"; // Синій жирний текст
                        } else {
                            style = "-fx-text-fill: grey;"; // Звичайний сірий для своїх повідомлень
                        }
                    }

                    setText(displayText);
                    setStyle(style);
                }
            }
        });

        // Клік по чату
        chatListView.setOnMouseClicked(e -> {
            Chat selected = chatListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (chatPoller != null) chatPoller.stop();
                showChatSessionScene(selected);
            }
        });

        root.getChildren().addAll(header, createBox, btnRefresh, chatListView);

        // --- АВТОМАТИЧНЕ ОНОВЛЕННЯ ІСТОРІЇ У ВСІХ ЧАТАХ ---
        chatPoller = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            refreshChatListWithHistory(chatListView);
        }));
        chatPoller.setCycleCount(Timeline.INDEFINITE);
        chatPoller.play();

        // Перше завантаження
        refreshChatListWithHistory(chatListView);

        primaryStage.setScene(new Scene(root, 400, 500));
    }

    // Допоміжний метод для оновлення і списку, і історії
    private void refreshChatListWithHistory(ListView<Chat> listView) {
        // 1. Отримуємо список чатів
        List<Chat> chats = manager.fetchChats();

        // 2. Проходимо по кожному чату і підтягуємо історію
        for (Chat c : chats) {
            try {
                // Завантажуємо повідомлення для конкретного чату
                manager.updateChatHistory(c, 0);
            } catch (Exception e) {
                System.out.println("Error updating history for chat: " + c.getName());
            }
        }

        // 3. Оновлюємо UI (запам'ятовуємо вибір, щоб не скакало)
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        listView.setItems(FXCollections.observableArrayList(chats));
        if (selectedIndex >= 0 && selectedIndex < chats.size()) {
            listView.getSelectionModel().select(selectedIndex);
        }
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
                msg.setStatus(MessageStatus.SENDING);
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
        // 1. Оновлюємо історію з сервера (як було)
        manager.updateChatHistory(chat, 0);

        ObservableList<String> items = FXCollections.observableArrayList();

        if (chat.getMessages() != null) {
            for (Message m : chat.getMessages()) {

                // --- ЛОГІКА ПРОЧИТАННЯ (НОВЕ) ---
                // Якщо повідомлення ВІД ІНШОГО користувача і статус ще не READ
                if (m.getSender() != null &&
                        !m.getSender().getNickName().equals(currentUser.getNickName())) {

                    // Перевіряємо поточний статус, щоб не ставити його зайвий раз
                    // (Вимагає імпорту: import Client.Model.MessageStatus;)
                    if (m.getStatus() != Client.util.MessageStatus.READ) {
                        m.setStatus(Client.util.MessageStatus.READ);
                        manager.sendReadStatus(m);
                    }
                }
                // ---------------------------------

                // Форматування рядка для відображення
                String time = (m.getSentTime() != null) ? m.getSentTime().toLocalTime().toString() : "..";
                String sender = (m.getSender() != null) ? m.getSender().getNickName() : "Unknown";

                // Можна додати візуальну позначку для своїх повідомлень (прочитано/ні)
                String statusMarker = "";
                if (m.getSender().getNickName().equals(currentUser.getNickName())) {
                    statusMarker = (m.getStatus() == Client.util.MessageStatus.READ) ? " (R)" : " (U)";
                }

                items.add(String.format("[%s] %s: %s%s", time, sender, m.getText(), statusMarker));
            }
        }

        // Оновлюємо UI, тільки якщо кількість повідомлень змінилась
        // (Або можна прибрати умову if, якщо хочете бачити зміну статусу (R)/(U) в реальному часі)
        if (view.getItems().size() != items.size()) {
            view.setItems(items);
            view.scrollTo(items.size() - 1); // Прокрутка вниз
        } else {
            // Якщо кількість та сама, але статуси могли змінитись - можна просто оновити список:
             view.setItems(items);
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