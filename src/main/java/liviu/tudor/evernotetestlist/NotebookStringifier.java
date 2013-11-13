package liviu.tudor.evernotetestlist;

import com.evernote.edam.type.Notebook;

public class NotebookStringifier implements Stringifier<Notebook> {
    public static final NotebookStringifier INSTANCE = new NotebookStringifier();

    private NotebookStringifier() {
        //singleton!
    }

    public String toString(Notebook object) {
        return object.getName() + " [" + object.getGuid() + "]";
    }
}
