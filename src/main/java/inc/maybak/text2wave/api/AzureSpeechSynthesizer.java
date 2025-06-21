package inc.maybak.text2wave.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AzureSpeechSynthesizer {

	@Value("${azure.speech.key}")
	private String apiKey;

	@Value("${azure.speech.region}")
	private String region;

	@Value("${azure.speech.endpoint}")
	private String endpoint;

	public byte[] synthesizeSpeech(String promptText) throws Exception {
		// Placeholder for Azure Speech API integration
		// This method would call the Azure Speech API to convert text to speech
		// and return the audio data as a byte array.

		// For now, we will return an empty byte array.
		// return new byte[0];

		URL url = new URL(endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Set headers
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Ocp-Apim-Subscription-Key", apiKey);
		connection.setRequestProperty("Content-Type", "application/ssml+xml");
		connection.setRequestProperty("X-Microsoft-OutputFormat", "riff-16khz-16bit-mono-pcm");
		connection.setRequestProperty("User-Agent", "TextToWaveApp");
		connection.setDoOutput(true);

		// Build SSML request body
		String ssml = "<speak version='1.0' xml:lang='en-US'>" +
				"<voice xml:lang='en-US' xml:gender='Female' name='en-US-JennyNeural'>" +
				promptText +
				"</voice></speak>";

		// Send request
		connection.getOutputStream().write(ssml.getBytes("UTF-8"));

		// Read response
		try (InputStream inputStream = connection.getInputStream();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			return outputStream.toByteArray();

		} catch (Exception e) {

			throw new RuntimeException("Failed to synthesize speech: " + e.getMessage(), e);

		} finally {

			connection.disconnect();
			
		}

	}

}
