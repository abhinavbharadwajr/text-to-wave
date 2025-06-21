package inc.maybak.text2wave.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;

@Component
public class PromptsController {

	// private String csvFilePath;
	// private String outputFolderPath;

	// public String getCsvFilePath() {
	// 	return csvFilePath;
	// }

	// public void setCsvFilePath(String csvFilePath) {
	// 	this.csvFilePath = csvFilePath;
	// }

	// public String getOutputFolderPath() {
	// 	return outputFolderPath;
	// }

	// public void setOutputFolderPath(String outputFolderPath) {
	// 	this.outputFolderPath = outputFolderPath;
	// }

	public Map<String, String> readPrompts(String csvFilePath) throws Exception {

		Map<String, String> prompts = new HashMap<>();

		try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
			String[] nextLine;
			boolean isFirstLine = true;

			while ((nextLine = reader.readNext()) != null) {
				if (isFirstLine) {
					isFirstLine = false; // skip header
					continue;
				}

				if (nextLine.length >= 2) {
					String promptId = nextLine[0].trim();
					String promptText = nextLine[1].trim();
					prompts.put(promptId, promptText);
				}
			}
		} catch (Exception e) {
			throw new Exception("Failed to read CSV: " + e.getMessage(), e);
		}

		return prompts;
	}

	public void writePrompts(String outputFolderPath, String promptId, byte[] audioData) throws IOException {
		if (audioData == null || audioData.length == 0) {
			throw new IOException("No audio data received for PromptID: " + promptId);
		}

		File folder = new File(outputFolderPath);
		if (!folder.exists()) {
			if (!folder.mkdirs()) {
				throw new IOException("Failed to create output directory: " + outputFolderPath);
			}
		}

		File outputFile = new File(folder, promptId + ".wav");

		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			fos.write(audioData);
		} catch (IOException e) {
			throw new IOException("Failed to write WAV file for PromptID: " + promptId, e);
		}
	}

}
