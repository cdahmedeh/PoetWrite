package net.cdahmedeh.poetwrite.ui;

public class TextUpdateEvent extends AppEvent {
    private String text = "";

    public TextUpdateEvent() {
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
