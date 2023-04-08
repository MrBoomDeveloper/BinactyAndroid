package com.mrboomdev.platformer.game.pack;

import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PackLoader {
	private static List<PackData.Manifest> packs;
	private static List<PackData.GamemodesRow> gamemodes;

	public static List<PackData.Manifest> getPacks() {
		if(packs == null) {
			packs = new ArrayList<PackData.Manifest>();
			reloadPacks();
		}
		return packs;
	}
	
	public static List<PackData.GamemodesRow> getGamemodes() {
		if(gamemodes == null) {
			gamemodes = new ArrayList<PackData.GamemodesRow>();
			reloadGamemodes();
		}
		return gamemodes;
	}
	
	public static void reloadGamemodes() {
		if(packs == null) reloadPacks();
		gamemodes.clear();
		try {
			Moshi moshi = new Moshi.Builder().build();
			JsonAdapter<List<PackData.GamemodesRow>> adapter = moshi.adapter(Types.newParameterizedType(List.class, PackData.GamemodesRow.class));
			for(var pack : packs) {
				if(pack.resources == null || pack.resources.gamemodes == null) continue;
				List<PackData.GamemodesRow> rows = adapter.fromJson(pack.source.goTo(pack.resources.gamemodes).readString(false));
				for(var row : rows) {
					for(var gamemode : row.data) {
						gamemode.source = pack.source.goTo(pack.resources.gamemodes).getParent();
						gamemode.author = pack.author;
					}
				}
				gamemodes.addAll(rows);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void reloadPacks() {
		if(packs == null) getPacks();
		packs.clear();
		try {
			Moshi moshi = new Moshi.Builder().build();
			JsonAdapter<List<PackData.Config>> adapter = moshi.adapter(Types.newParameterizedType(List.class, PackData.Config.class));
			var configs = adapter.fromJson(FileUtil.external("packs/installed.json").readString(false));
			for(var config : configs) {
				JsonAdapter<PackData.Manifest> manifestAdapter = moshi.adapter(PackData.Manifest.class);
				var manifest = manifestAdapter.fromJson(config.file.goTo("manifest.json").readString(false));
				manifest.source = config.file.goTo("");
				packs.add(manifest);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}