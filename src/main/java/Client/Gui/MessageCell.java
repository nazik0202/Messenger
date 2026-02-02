package Client.Gui;

import Client.Model.Message;
import Client.Model.User;
import Client.util.MessageStatus;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MessageCell extends ListCell<String> { // Using String for demo, ideally use Message object directly if ListView<Message>
    private final User currentUser;

    public MessageCell(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            // Parse your formatted string back or change ListView to ListView<Message>
            // For this example, assuming item is "Nick: MessageText"
            boolean isMyMessage = item.startsWith(currentUser.getNickName());
            String textContent = item.substring(item.indexOf(":") + 1).trim();

            // 1. Container for alignment
            HBox container = new HBox();
            container.setFillHeight(true);

            // 2. The Bubble
            Label bubble = new Label(textContent);
            bubble.setWrapText(true);
            bubble.setMaxWidth(300); // Max width before wrapping

            // 3. Styling
            if (isMyMessage) {
                container.setAlignment(Pos.CENTER_RIGHT);
                bubble.getStyleClass().add("sent-bubble");
            } else {
                container.setAlignment(Pos.CENTER_LEFT);
                bubble.getStyleClass().add("received-bubble");
            }

            // 4. Time/Status (Optional)
            VBox bubbleContent = new VBox();
            if (isMyMessage) {
                bubbleContent.setAlignment(Pos.BOTTOM_RIGHT);
            } else {
                bubbleContent.setAlignment(Pos.BOTTOM_LEFT);
            }
            bubbleContent.getChildren().add(bubble);

            container.getChildren().add(bubbleContent);

            setGraphic(container);
            setText(null); // Clear plain text
        }
    }
}
