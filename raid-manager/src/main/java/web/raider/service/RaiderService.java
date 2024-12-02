package web.raider.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.gson.JsonElement;

import web.raider.model.RaiderVo;
import web.util.GoogleUtil;
import web.util.WclUtil;

@Service
public class RaiderService {
	
	private final Logger logger = LogManager.getLogger(RaiderService.class);
	
	@Autowired
	WclUtil wclUtil;
	
	@Autowired
	GoogleUtil googleUtil;
	
	@Value("${google.sheetId}")
    private String sheetId;
	
	
	public ValueRange getRaiderSheetData (String sheetId, String range) throws FileNotFoundException, IOException, GeneralSecurityException {	    
		ValueRange resultvalueRange = googleUtil.getGoogleSheetData(sheetId, range);
		
		return resultvalueRange;
	}
	
	public void setRaiderLogs (List<List<Object>> values, int difficulty) throws GeneralSecurityException, IOException {
		String range = "";
		
		if ( 5==difficulty ) {
			range = "로그!A2:N21";
		} else if ( 4==difficulty ) {
			range = "로그!A24:N43";
		}
		
		googleUtil.setGoogleSheetData(sheetId, range, values);
	}
	
	public List<RaiderVo> getRaiderList () throws IOException, GeneralSecurityException {
	    String sheetId = "1Qtlk0226rENsTjKBXNQ6lyAXWamzOLGJ74y1IrE3AYg";
	    String range = "인원정보!A1:P28";
	    
	    ValueRange tmpValueRange = getRaiderSheetData(sheetId, range);
	    
	    Map<String, Object> resultMap = new HashMap<String, Object>();
	    
	    List<List<Object>> tmpValues = tmpValueRange.getValues();
	    
	    List<RaiderVo> raiderList = new ArrayList<RaiderVo>();
	    
	    for ( int i=1; i<21; i++) {
	    	RaiderVo vo = new RaiderVo();
	    	vo.setNo(Integer.parseInt((String) tmpValues.get(i).get(0)));
	    	vo.setRole((String) tmpValues.get(i).get(1));
	    	vo.setName((String) tmpValues.get(i).get(2));
	    	vo.setServer((String) tmpValues.get(i).get(3));
	    	vo.setAlias((String) tmpValues.get(i).get(4));
	    	
	    	raiderList.add(vo);
    	}
	    
	    return raiderList;
	}
	
	public List<List<Object>> getCharacterLogs(List<RaiderVo> raiderList, int difficulty) {
		logger.info("@RaiderService > getCharacterLogs");
		String result = wclUtil.getWclLogs(wclUtil.getCharacterLogsQuery(raiderList, difficulty));
		JsonElement element = wclUtil.parseWclApiData(result);
		
		List<List<Object>> resultList = new ArrayList<List<Object>>();
		
		for ( int i=0; i<raiderList.size(); i++ ) {
			RaiderVo vo = raiderList.get(i);
			
			List<Object> tmpList = new ArrayList<Object>();
			tmpList.add(vo.getNo());
			tmpList.add(vo.getRole());
			tmpList.add(vo.getName());
			tmpList.add(vo.getServer());
			tmpList.add(vo.getAlias());
			
			String [] tmpArr = new String [9];
			tmpArr = wclUtil.getRankPercent(element, vo.getNo());
			
			for ( int j=0; j<tmpArr.length; j++ ) {
				tmpList.add(tmpArr[j]);
			}
			
			resultList.add(tmpList);
		}
		
		return resultList;
	}
	
	public String getAccessToken() {
		return wclUtil.getAccessToken();
	}
	
}
