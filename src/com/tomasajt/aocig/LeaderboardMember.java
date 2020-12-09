package com.tomasajt.aocig;

import org.json.simple.JSONObject;

public class LeaderboardMember {

	String id;
	String name;
	long local_score;
	long global_score;
	long stars;
	long last_star_ts;

	public LeaderboardMember(JSONObject jsonObject) {
		id = jsonObject.get("id").toString();
		name = jsonObject.get("name").toString();
		local_score = (Long) jsonObject.get("local_score");
		global_score = (Long) jsonObject.get("global_score");
		stars = (Long) jsonObject.get("stars");
		last_star_ts = Long.parseLong(jsonObject.get("last_star_ts").toString());
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getLocalScore() {
		return local_score;
	}

	public long getGlobalScore() {
		return global_score;
	}

	public long getStars() {
		return stars;
	}

	public long getLastStarTs() {
		return last_star_ts;
	}
}
