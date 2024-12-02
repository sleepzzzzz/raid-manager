package web.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

@Configuration
public class GoogleUtil {
	
	@Value("${google.credentialsFilePath}")
    private String credentialsFilePath;
	
	@Value("${google.applicationName}")
    private String applicationName;
	
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	
	private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        ClassLoader loader = GoogleUtil.class.getClassLoader();
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(loader.getResource(credentialsFilePath).getFile())).createScoped(SCOPES);

        return credential;
    }
	
	public ValueRange getGoogleSheetData(String sheetId, String range) throws GeneralSecurityException, IOException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName(applicationName).build();
		ValueRange tmpValueRange = service.spreadsheets().values().get(sheetId, range).execute();
		
		return tmpValueRange;
	}
	
	public void setGoogleSheetData(String sheetId, String range, List<List<Object>> values) throws GeneralSecurityException, IOException {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		
	    ValueRange data = new ValueRange().setValues(values);

	    Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
	    		.setApplicationName(applicationName)
	    		.build();

	    service.spreadsheets().values().update(sheetId, range, data)
	    .setValueInputOption("USER_ENTERED")
	    .execute();
	}

}