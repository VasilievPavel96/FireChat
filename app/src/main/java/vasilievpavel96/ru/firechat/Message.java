package vasilievpavel96.ru.firechat;

public class Message {
    public String authorUid;
    public String author;
    public String message;

    public Message() {
    }

    public Message(String authorUid, String author, String message) {
        this.authorUid = authorUid;
        this.author = author;
        this.message = message;
    }
}
