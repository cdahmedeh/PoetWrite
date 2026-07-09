package net.cdahmedeh.poetwrite.ui.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class FileDialogNeededEvent extends AppEvent {
    @Getter
    @Setter
    private boolean needed = true;
}