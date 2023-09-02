package com.mrboomdev.binacty.packs;

import com.squareup.moshi.Json;
public class PackEntry {
	@Json(name = "script_path")
	public String scriptPath;
	@Json(name = "map_path")
	public String mapPath;
	@Json(name = "level_id")
	public String levelId;
}