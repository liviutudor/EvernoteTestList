package liviu.tudor.evernotetestlist;

import com.evernote.edam.type.Note;

public class NoteStringifier implements Stringifier<Note> {
    public static final NoteStringifier INSTANCE = new NoteStringifier();

    private NoteStringifier() {
        // singleton!
    }

    public String toString(Note object) {
        return object.getTitle() + " [" + object.getGuid() + "]";
    }

}
