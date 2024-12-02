package web.raider.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RaiderVo {
	private int no;
	private String role;
	private String name;
	private String server;
	private String alias;
	
	private String job;
	private String type;
	private String gear;
	private String tier;
	private String synergy;
	
	private String[] rankPercent;
}
