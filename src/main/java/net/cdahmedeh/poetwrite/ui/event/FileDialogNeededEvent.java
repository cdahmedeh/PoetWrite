package net.cdahmedeh.poetwrite.ui.event;

//@NoArgsConstructor
public class FileDialogNeededEvent extends AppEvent {
//    @Getter
//    @Setter
    private boolean needed = true;

    public FileDialogNeededEvent() {
        System.out.println("FileDialogNeededEvent");
    }

    public boolean isNeeded() {
        return needed;
    }

    public void setNeeded(boolean needed) {
        this.needed = needed;
    }
}