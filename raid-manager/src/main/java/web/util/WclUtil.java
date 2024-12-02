package web.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import web.raider.model.RaiderVo;

@Configuration
public class WclUtil {
	
	@Value("${wcl.baseUrl}")
    private String baseUrl;
	
	@Value("${wcl.clientId}")
    private String clientId;
	
    @Value("${wcl.clientSecret}")
    private String clientSecret;
	
	private final Logger logger = LogManager.getLogger(WclUtil.class);
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	public String getAccessToken() {

        // Headers 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        // Body 설정
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);

        // Body를 URL 인코딩된 문자열로 변환
        StringBuilder bodyString = new StringBuilder();
        body.forEach((key, value) -> {
            if (bodyString.length() > 0) {
                bodyString.append("&");
            }
            bodyString.append(key).append("=").append(value);
        });

        // HttpEntity 생성
        HttpEntity<String> entity = new HttpEntity<>(bodyString.toString(), headers);

        // POST 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
        		baseUrl,
        		HttpMethod.POST,
        		entity,
        		String.class
        );

        // 응답 본문 반환
        return response.getBody();
    }
	
	public String getCharacterLogsQuery(List<RaiderVo> raiderList, int difficulty) {
		String query = "";
		
		for ( int i=0; i<raiderList.size(); i++ ) {
			RaiderVo vo = raiderList.get(i);
			
			String metric = "dps";
			if ( "힐".equals(vo.getRole())) {
				metric = "hps";
			}
			
			logger.info("@WclUtil > getCharacterLogsQuery > param: " + i + "/" + vo.getNo() + "/" + vo.getName() + "/" + vo.getServer() + "/" +  difficulty + "/" + metric);
			
			String tmpQuery = """
					%s: character(name: "%s", serverSlug: "%s", serverRegion: "KR") {
						avg: zoneRankings(zoneID: 38, difficulty: %s, metric: %s)
						named1: encounterRankings(encounterID: 2902, difficulty: %s, metric: %s)
						named2: encounterRankings(encounterID: 2917, difficulty: %s, metric: %s)
						named3: encounterRankings(encounterID: 2898, difficulty: %s, metric: %s)
						named4: encounterRankings(encounterID: 2918, difficulty: %s, metric: %s)
						named5: encounterRankings(encounterID: 2919, difficulty: %s, metric: %s)
						named6: encounterRankings(encounterID: 2920, difficulty: %s, metric: %s)
						named7: encounterRankings(encounterID: 2921, difficulty: %s, metric: %s)
						named8: encounterRankings(encounterID: 2922, difficulty: %s, metric: %s)
    			    }
					""";
			tmpQuery = String.format(tmpQuery,
	        		"character"+(i+1), vo.getName(), vo.getServer(),
	        		//difficulty, metric,
	        		difficulty, metric,
	        		difficulty, metric,
	        		difficulty, metric,
	        		difficulty, metric,
	        		difficulty, metric,
	        		difficulty, metric,
	        		difficulty, metric,
	        		difficulty, metric,
	        		difficulty, metric
	        		);
			query += tmpQuery;
		}
		
        query = """
    			{
    				characterData {
        		 		"""
        		 		+ query +
        				"""
	 				}
    			}
                """;
        
       logger.info(query);
		return query;
	}

    public String getWclLogs(String query) {
        String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI5ZDgzYjlkZC00MGJjLTQ4ODUtOTljNy00ZGMxZTA5ZGYzZGMiLCJqdGkiOiI4YWJhYmIyNjFlOWJjNjA3YWUzMzdiMjBlNjIzNjI2YWMyNzMyNmZkODJmNjZlMjUzNjZiZjA4MDBmNGYyYTY5OWEzZTE0NDY1NzgxYjQyNCIsImlhdCI6MTczMjE1NDEzNy42OTQwNzIsIm5iZiI6MTczMjE1NDEzNy42OTQwNzQsImV4cCI6MTc2MzI1ODEzNy42ODY0OTQsInN1YiI6IiIsInNjb3BlcyI6WyJ2aWV3LXVzZXItcHJvZmlsZSIsInZpZXctcHJpdmF0ZS1yZXBvcnRzIl19.PzlokxSk87gIeRFHajmA_JK7u2ngN5ZVpi0ouTDFjtTRIAG6IX2C7YTR2ueeT_0v90BvuIZTWswZrinB8O43lRBHuIaf8dSS6l6Mv63bEHB7y1ReTAySktBXkwyiZKkAZ4ECub2exV79h_WWbWpww-jUvhOq5hqbjP3XbqziiuLxIozXfybZ7Os-kP-YsKfg_dQFQPCd_7Go1EY04ZGjoUxrZ12zPRxG1W5GZ-vv3plJYuwqQoYt7hqqb_k0dtWDmXhrceNULTyiGnOWEjt0te675qh_YH2CYGRNVzCCacQEi_UhoUUNgbtxwjTy_hiOfAbsMDz7BUuHZSb8MeqkG7WVw797twiUGW9C7U3rOdq_QTrArYAcdlYhDEsMiU8jg7zeG3I8jX0qb2i7LsHv7pCLnkxefM33G_LCIStOefsSsswsLrvgcLDdd0FxnLQg-3FTNCgpmlUya4P9tkE9oq6Wdf_UG4ApsLpnGfrNqixP4mu4wfNJfpQUlRqBozjwc3xF1CPWJigqX4uNKiO-z_ZTZcdPK3V7wTbd6Uyzjr03DP-5UiUrW9avwDmmNMA5lmekflYpjBriOPaQBMkxV9t7HAvYPvo8J07G3J0ArCm7TzY-KcF6jugCWN95vX6HMFqW5T6yjYoP1JcN6O20MfRuKuMKZGzqAMxrDyN4TfY";
        String baseUrl = "https://www.warcraftlogs.com/api/v2/client";
        
        String result = "";
        
        // 요청 본문 생성
		JSONObject requestBody = new JSONObject();
		requestBody.put("query", query);

        // 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey); // Bearer Token 설정

        // 요청 생성
		HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

		// RestTemplate을 사용하여 요청 전송
		RestTemplate restTemplate = new RestTemplate();
		try {
		    ResponseEntity<String> response = restTemplate.exchange(
		    		baseUrl,
					HttpMethod.POST,
					request,
					String.class
		    );
		    // 응답 처리
			if (response.getStatusCode() == HttpStatus.OK) {
			    logger.info("@WclUtil > getWclLogs > Response: " + response.getBody());
			    result = response.getBody();
			} else {
			    logger.info("@WclUtil > getWclLogs > Failed: " + response.getStatusCode());
		    }
		} catch (Exception e) {
		    System.err.println("Error during GraphQL call: " + e.getMessage());
		} finally {
			return result;
		}
    }
    
    public JsonElement parseWclApiData ( String str ) {
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(str);
		
		return element;
    }
    
	public String[] getRankPercent ( JsonElement element, int characterNo ) {
		String[] resultArr = new String[9];
		JsonObject dataObj = element.getAsJsonObject().get("data").getAsJsonObject();
		JsonObject characterDataObj = dataObj.getAsJsonObject().get("characterData").getAsJsonObject();
		JsonObject characterObj = characterDataObj.getAsJsonObject().get("character"+characterNo).getAsJsonObject();
		
		// 종합 점수 구하기
		JsonObject avgObj = characterObj.getAsJsonObject().get("avg").getAsJsonObject();
		double bestPerfAvg = avgObj.get("bestPerformanceAverage").getAsDouble();
		bestPerfAvg = Math.round(bestPerfAvg*100)/100.0;
		
		resultArr[0] = Double.toString(bestPerfAvg);
		
		// 네임드별 점수 구하기
		for ( int i=0; i<8; i++ ) {
			JsonObject namedObj = characterObj.getAsJsonObject().get("named"+(i+1)).getAsJsonObject();
			JsonArray ranksArr = namedObj.getAsJsonObject().get("ranks").getAsJsonArray();
			
			if ( !ranksArr.isEmpty() ) {
				JsonObject rankObj = (JsonObject) ranksArr.get(0);
				double rankPercent = rankObj.get("rankPercent").getAsDouble();
				rankPercent = Math.round(rankPercent*100)/100.0;
				
				resultArr[i+1] = Double.toString(rankPercent); 
			} else {
				resultArr[i+1] = "";
			}
		}
		
		return resultArr;
		
	}
	
}