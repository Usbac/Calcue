package ve.com.usbac.calcue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.stage.FileChooser;

public final class FileManager {
    
    private final String NEW_LINE = System.getProperty("line.separator");
    private final String ERROR_FILE = "ERROR: Bad file structure";
    private final String ERROR_IO = "ERROR: IO Exception";
    private final String VARIABLES_TAG = "<Variables>";
    private final String OPERATIONS_TAG = "<Operations>";
    private final String FILE_FORMAT = "*.calc";
    public final FileChooser FILE_CHOOSER = new FileChooser();
    
    String filePath = "";
    Controller controller;
    Model model;
    
    
    public FileManager(Controller c, Model m) {
        controller = c;
        model = m;
        FILE_CHOOSER.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Text doc(" + FILE_FORMAT + ")", FILE_FORMAT));
    }

    
    public void openFile() throws FileNotFoundException {
        File file = FILE_CHOOSER.showOpenDialog(Controller.stage);
        if (file != null) {
            filePath = file.toString();
            readFromFile(file);
        }
    }
    
    
    public void saveFile() throws IOException {
        File saveFile = new File(filePath);
        if (saveFile.exists()) {
            saveFile.delete();
            writeToFile(saveFile);
        } else {
            saveFileAs();
        }
    }
    
    
    public void saveFileAs() throws IOException {
        writeToFile(FILE_CHOOSER.showSaveDialog(Controller.stage));
    }
    
    
    public void writeToFile(File file) throws IOException {
        FileWriter out;
        try {
            filePath = file.toString();
        } catch (Exception e) {
            return;
        }
        out = new FileWriter(file.toString());
        out.write(VARIABLES_TAG);
        out.write(NEW_LINE);
        out.write(controller.getVariables().replace("\n", NEW_LINE));
        out.write(NEW_LINE);
        out.write(OPERATIONS_TAG);
        out.write(NEW_LINE);
        out.write(model.getOperations());
        out.close();
    }
    
    
    public void readFromFile(File file) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
        String auxText;
        model.clearAll();
        try {
            if (VARIABLES_TAG.equals(auxText = br.readLine())) {
                while((auxText = br.readLine()) != null && !OPERATIONS_TAG.equals(auxText))
                    controller.variables.setText(controller.getVariables() + auxText + NEW_LINE);
                
                if (OPERATIONS_TAG.equals(auxText)) {
                    while((auxText = br.readLine()) != null)
                        model.addToOperations(auxText);
                    model.updateLastOperations();
                    return;
                }
            }
            controller.variables.setText(ERROR_FILE);
        } catch (IOException e) {
            controller.variables.setText(ERROR_IO);
        }
    }
}