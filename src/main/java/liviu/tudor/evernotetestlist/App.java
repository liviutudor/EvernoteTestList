package liviu.tudor.evernotetestlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;

/**
 * Program entry point. Create a <code>.evernotetestlist</code> properties file
 * in your home directory with the dev token:
 *
 * <pre>
 *    devtoken=....
 * </pre>
 */
public class App {
    /** Change this to EvernoteService.PRODUCTION for live. */
    private static final EvernoteService SERVICE         = EvernoteService.SANDBOX;

    /** Look for all the &lt;li&li; items in the document. */
    private static final String          PATH_LIST_ITEM = "li";

    /** Name of the config file (in user home). */
    private static final String          INI_FILE        = ".evernotetestlist";

    private NoteStoreClient              noteStore;

    public App(String devToken) throws EDAMUserException, EDAMSystemException, TException {
        EvernoteAuth evernoteAuth = new EvernoteAuth(SERVICE, devToken);
        ClientFactory factory = new ClientFactory(evernoteAuth);
        noteStore = factory.createNoteStoreClient();
    }

    public Notebook selectNotebook() {
        try {
            List<Notebook> notebooks = noteStore.listNotebooks();
            return selectFromList("Select notebook", notebooks, NotebookStringifier.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Note selectNote(Notebook book) {
        NoteFilter filter = new NoteFilter();
        filter.setNotebookGuid(book.getGuid());
        filter.setOrder(NoteSortOrder.TITLE.getValue());
        filter.setAscending(true);
        try {
            NoteList lst = noteStore.findNotes(filter, 0, 1000);
            List<Note> notes = lst.getNotes();
            return selectFromList("Select note", notes, NoteStringifier.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> extractListItems(Note note) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        if (note == null)
            return null;
        String content = noteStore.getNoteContent(note.getGuid());
        Document doc = Jsoup.parse(content);
        Elements el = doc.select(PATH_LIST_ITEM);
        List<String> items = new ArrayList<String>(el.size());
        for (Element e : el) {
            items.add(e.text());
        }
        return items;
    }

    public void run() throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        Notebook nb = selectNotebook();
        Note note = selectNote(nb);
        List<String> items = extractListItems(note);
        if (items == null || items.size() == 0) {
            System.out.println("=== No list found.");
        } else {
            System.out.println("LIST ITEMS");
            for (String i : items) {
                System.out.println("* " + i);
            }
        }
    }

    public static <T> T selectFromList(String title, List<T> list, Stringifier<T> str) {
        if (list == null)
            return null;
        System.out.println(title);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + " ) " + str.toString(list.get(i)));
        }
        Scanner s = new Scanner(System.in);
        String sNo = s.nextLine();
        try {
            int n = Integer.parseInt(sNo);
            return list.get(n);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            App app = new App(readDevToken());
            app.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readDevToken() throws IOException {
        File file = new File(System.getProperty("user.home"), INI_FILE);
        Properties p = new Properties();
        p.load(new FileInputStream(file));
        return p.getProperty("devtoken");
    }
}
