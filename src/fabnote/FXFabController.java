
package fabnote;

import fabFileLib.fabIO;
import hagumafab.FrequencyMap;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import jefixePack.jfxFileIO;

public class FXFabController implements Initializable {
    
    @FXML private Label label;
    @FXML private TextField textField;
    @FXML private TextArea textArea;
    @FXML private TextArea hashTagArea;
    @FXML private RadioButton editRadio;
    @FXML private RadioButton allowDelete;
    @FXML private ListView<String> listView;
    @FXML private Button removeButton;
    
    List<Note> noteList;
    fabIO myIO = new fabIO();
    jfxFileIO jfx = new jfxFileIO();
    SQLiteDB theDB ;
    
    String initialFolder;
    ObservableList<String> observableList = FXCollections.observableArrayList();
    List<Integer> observableNoteListIndices;
    int currentIndexInList = 0;  /* INDEX OF CURRENT NOTE FROM NOTELIST */
    int selectedFromObservableList = 0;
    
    @FXML
    private void saveNote(ActionEvent event) {
        noteList.get(currentIndexInList).setContent(textArea.getText());
        String hashtagText = hashTagArea.getText();
        if (hashtagText.isEmpty()) {
            noteList.get(currentIndexInList).setNewTags("untagged");
        } else  noteList.get(currentIndexInList).setNewTags(hashtagText);
        
            try { 
                theDB.updateDB(currentIndexInList + 1, noteList.get(currentIndexInList).getLastTime(), 
                        noteList.get(currentIndexInList).getHashTags(), textArea.getText());
                
            } catch (SQLException ex) {
                label.setText("Error in updating database"); // ex.printStackTrace();
            } 
            
    }
    
    @FXML
    private void radioBTNClick(ActionEvent event) {
        boolean radioState = editRadio.isSelected();
        hashTagArea.setEditable(radioState);
    }
    
    @FXML
    private void listAllNotes(ActionEvent event) {
        observableList.clear();
        observableNoteListIndices.clear();
        for(int i=0 ; i<noteList.size() ; i++){
            observableList.add(noteList.get(i).getPreview()); 
            observableNoteListIndices.add(i);
        }
    }
    
    @FXML
    private void searchNote(Event event) {
        List<FrequencyMap> fMapList = new java.util.ArrayList<>();
        String[] lesHashes = textField.getText().split("[ ,;#]");
        for(int i=0 ; i<noteList.size() ; i++){
            int frequency = noteList.get(i).tagsMatched(lesHashes);
            if (frequency > 0) {
                fMapList.add(new FrequencyMap(i, frequency));
            }
        } java.util.Collections.sort(fMapList, FrequencyMap.compareByFrequency);
        
        observableList.clear();
        observableNoteListIndices.clear();
        for(int i=0 ; i<fMapList.size() ; i++){
            int index = (int) fMapList.get(i).getObject(); /* INDEX OF NOTE WITH THIS HIGH FREQUENCY */
            
            observableList.add(noteList.get(index).getPreview());
            observableNoteListIndices.add(index);
        }
    }
    
    @FXML
    public void selectFromListView(Event event){
        selectedFromObservableList = listView.getSelectionModel().getSelectedIndex();
        int indexInNotelist = observableNoteListIndices.get(selectedFromObservableList);
        
        textArea.setText(noteList.get(indexInNotelist).getContent());
        String hashTagsDisplay = noteList.get(indexInNotelist).getHashTags();
        hashTagArea.setText(hashTagsDisplay.substring(1, hashTagsDisplay.length()-1));
        currentIndexInList = indexInNotelist;
    }
    
    @FXML
    private void removeNote(ActionEvent event) {
        
        if (allowDelete.isSelected()) {
            int idToDelete = noteList.get(currentIndexInList).getId();
            noteList.remove(currentIndexInList);
            try {
                theDB.deleteFromDatabase(idToDelete);
            } catch (SQLException ex) {
                label.setText("Error in deleting from database");
            }
            observableList.remove(selectedFromObservableList);
            observableNoteListIndices.remove(Integer.valueOf(currentIndexInList)); 
            allowDelete.setSelected(false);
            switchVisibiltyOfAllowDelete();
            removeButton.setText("Remove Note");
        }
        else{
            switchVisibiltyOfAllowDelete();
            String btnText = removeButton.getText();
            if (btnText.equals("Ok & Delete")) {
                removeButton.setText("Remove Note");
            } else removeButton.setText("Ok & Delete");
        }
    }
    
    private void switchVisibiltyOfAllowDelete(){
        boolean status = allowDelete.isVisible();
        allowDelete.setVisible(!status);
//        if (status) {
//            removeButton.setText("Now Delete");
//        }else removeButton.setText("Remove Note");
    }
    
    @FXML
    private void addNewNote(ActionEvent event) {
        noteList.add(new Note());
        currentIndexInList = noteList.size() -1;
        textArea.setText("Text...");
        hashTagArea.setText("HahTags...");
        try {
            theDB.addToDatabase(noteList.get(currentIndexInList).getLastTime(), "untagged", "");
        } catch (SQLException e) {
            label.setText("Error in inserting new note into database");   // e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        noteList = new ArrayList<>();
        observableNoteListIndices = new ArrayList<>();
        initialFolder = jfx.defaultDirectory();
        theDB = new SQLiteDB(initialFolder + "\\Jefixe\\garage\\blackhole.db");
        try {
            theDB.addAllNotes(noteList);
        } catch (SQLException ex) { ex.printStackTrace();
        }
        listView.setItems(observableList);
        
        textField.setOnKeyPressed(event ->{
            if (event.getCode() == KeyCode.ENTER) searchNote(event);
        });
        
    }    
    
    
}
