package inc.maybak.text2wave.controller;

import java.io.File;
import java.util.Map;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import inc.maybak.text2wave.utils.ApplicationLogger;
import org.slf4j.Logger;

@Component
public class ApplicationController {

	private static final Logger logger = ApplicationLogger.getLogger(ApplicationController.class);

	@FXML
	private TextField csvFilePath;

	@FXML
	private TextField outputFolderPath;

	@FXML
	private TextArea statusArea;

	private Stage primaryStage;

	@FXML
	private ProgressBar progressBar;

	@Autowired
	private AzureSpeechSynthesizer azureSpeechSynthesizer;

	@Autowired
	private PromptsController promptsController;

	public void setPrimaryStage(Stage stage) {
		this.primaryStage = stage;
	}

	@FXML
	private void handleBrowseCSV() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select CSV File");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
		File selectedFile = fileChooser.showOpenDialog(primaryStage);
		if (selectedFile != null) {
			csvFilePath.setText(selectedFile.getAbsolutePath());
		}
	}

	@FXML
	private void handleBrowseFolder() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select Output Folder");
		File selectedFolder = directoryChooser.showDialog(primaryStage);
		if (selectedFolder != null) {
			outputFolderPath.setText(selectedFolder.getAbsolutePath());
		}
	}

	@FXML
	private void handleStartProcessing() {
		String csvPath = csvFilePath.getText();
		String outputPath = outputFolderPath.getText();

		if (csvPath.isEmpty() || outputPath.isEmpty()) {
			statusArea.appendText("Please select both CSV file and output folder.\n");
			return;
		}

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Map<String, String> prompts = promptsController.readPrompts(csvPath);
					int total = prompts.size();
					int count = 0;

					for (Map.Entry<String, String> entry : prompts.entrySet()) {
						String promptId = entry.getKey();
						String promptText = entry.getValue();

						Platform.runLater(() -> statusArea.appendText("Processing: " + promptId + "\n"));

						byte[] audioData = azureSpeechSynthesizer.synthesizeSpeech(promptText);
						promptsController.writePrompts(outputPath, promptId, audioData);

						count++;
                    	updateProgress(count, total);

						Platform.runLater(() -> statusArea.appendText("Saved: " + promptId + ".wav\n"));
					}

					Platform.runLater(() -> statusArea.appendText("All prompts processed successfully.\n"));

				} catch (Exception e) {
					
					Platform.runLater(() -> statusArea.appendText("Error: " + e.getMessage() + "\n"));
					logger.error("Exception occurred while processing prompts", e);

				}
				return null;
			}
		};

		progressBar.progressProperty().bind(task.progressProperty());
		new Thread(task).start();
		
	}

}
