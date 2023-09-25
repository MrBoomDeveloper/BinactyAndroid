package com.mrboomdev.platformer.game;

import java.util.ArrayList;
import java.util.List;

public class GameStatistics {
	public boolean isWin = false;
	public int totalKills = 0;
	public int totalDamage = 0;

	public List<StatEntry> getAsArray() {
		var array = new ArrayList<StatEntry>();

		array.add(new StatEntry("Total kills", totalKills));
		array.add(new StatEntry("Total damage", totalDamage));

		int gotCoins = totalKills * 2 + totalDamage / 200;

		var save = GameHolder.getInstance().launcher.getSave();
		save.edit().putInt("coins", (save.getInt("coins", 0) + gotCoins)).apply();

		array.add(new StatEntry("Got coins", gotCoins));
		array.add(new StatEntry("Got diamonds", 0));

		return array;
	}

	public static class StatEntry {
		private final String title;
		private String value;

		public StatEntry(String title, float value) {
			this.title = title;
			this.value = String.valueOf(value);

			if(this.value.endsWith(".0")) {
				this.value = this.value.substring(0, this.value.length() - 2);
			}
		}

		public String getTitle() {
			return title;
		}

		public String getValue() {
			return value;
		}
	}
}