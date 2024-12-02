package web.raider.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import web.raider.model.RaiderVo;
import web.raider.service.RaiderService;

@Controller
public class RaiderController {
	
	private final Logger logger = LogManager.getLogger(RaiderController.class);
	
	@Autowired
	RaiderService raiderService;
	
	@PostMapping("/raider/getRaiderList")
	@ResponseBody
	public String getRaiderList () throws IOException, GeneralSecurityException {
		logger.info("@RaiderController > getRaiderList");
		
		return raiderService.getRaiderList().toString();
	}
	
	@GetMapping("/raider/getRaiderLogs/{difficulty}")
	@ResponseBody
	public void getRaiderLogsByDifficulty (@PathVariable("difficulty") int difficulty) throws IOException, GeneralSecurityException {
		logger.info("@RaiderController > getRaiderLogs");
		List<RaiderVo> raiderList = raiderService.getRaiderList();
		List<List<Object>> resultList = raiderService.getCharacterLogs(raiderList, difficulty);
		
		raiderService.setRaiderLogs(resultList, difficulty);
	}
	
	@GetMapping("/raider/getAccessToken")
	@ResponseBody
	public String getAccessToken () {
		logger.info("@RaiderController > getAccessToken");
		return raiderService.getAccessToken();
	}

}