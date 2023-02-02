package net.bnjrKemal.MissionV4;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Data {
	
	private static HashMap<UUID, HashMap<String, Integer>> hashmap;
	private static HashMap<UUID, Integer> playerandrank;
	private static String[] ranks;
	private static final File file = new File(Main.getInstance().getDataFolder(), "/data.yml");
	private static YamlConfiguration yaml;
	private static HashMap<UUID, Integer> offlineplayertime = new HashMap<>();
	
	public Data() {
		hashmap = new HashMap<>();
		playerandrank = new HashMap<>();
		Main.getInstance().getConfig().options().copyDefaults();
		Main.getInstance().saveDefaultConfig();
		
		if(!file.exists()) {
			Main.getInstance().getDataFolder().mkdirs();
			try {
				file.createNewFile();
			}
			catch(IOException e){
				e.printStackTrace();	
			}
		}
		yaml = YamlConfiguration.loadConfiguration(file);
		fromYAMLtoHashMap();
	}

	public static void fromYAMLtoHashMap() {
		
		int size = 0;
		for(@SuppressWarnings("unused") String s : Main.getInstance().getConfig().getConfigurationSection("ranks").getKeys(false)) size += 1;
		ranks = new String[size + 1];
		System.out.println("Ranks.length: " + ranks.length + "");
		System.out.println("Size: " + size + "");
		int i = 0;
		for(String rank : Main.getInstance().getConfig().getConfigurationSection("ranks").getKeys(false)) {
			ranks[i++] = rank;
		}
		ranks[i] = "";
		if(getYAML().getConfigurationSection("data") != null) {

			for(String node : getYAML().getConfigurationSection("data.").getKeys(false)) {

				UUID uuid = UUID.fromString(node);
	
				HashMap<String, Integer> seconderHashMap = new HashMap<>();
				
				for(String key : getYAML().getConfigurationSection("data." + node + ".").getKeys(false)) {
							
					if(key.equals("rankid")) {
						int rankid = getYAML().getInt("data." + node + ".rankid");
						getPlayerAndRank().put(uuid, rankid);
					}  else if(key.equals("online-time")) {
						int value = getYAML().getInt("data." + node + ".online-time");
						getOfflinePlayerTime().put(uuid, value);
					} else {
						int value = getYAML().getInt("data." + node + "." + key);
						seconderHashMap.put(key, value);
					}
				}
				getHashMap().put(uuid, seconderHashMap);
				getYAML().set("data." + uuid, null);
			}
		}
		try {getYAML().save(getFile());} catch (IOException e) {e.printStackTrace();}
	}
	public static void fromHashmapToYAML() {
		
		for(Map.Entry<UUID, HashMap<String, Integer>> hm : getHashMap().entrySet()) {
			String uuid = hm.getKey().toString();
			int rankid = getPlayerAndRank().getOrDefault(hm.getKey(), 0);
			int onlinetime = getOnlineTime(hm.getKey());
			
			for(Map.Entry<String, Integer> hm2 : hm.getValue().entrySet()) {
				Object obj = hm2.getKey();
				Integer point = hm2.getValue();
				getYAML().set("data." + uuid + "." + obj, point);
			}
			getYAML().set("data." + uuid + ".rankid", rankid);
			getYAML().set("data." + uuid + ".online-time", onlinetime);
		}
		try {getYAML().save(getFile());} catch (IOException e) {e.printStackTrace();}
		
	}

	public static YamlConfiguration getYAML() { return yaml;}
	public static File getFile() { return file; }

	public static HashMap<UUID, Integer> getPlayerAndRank(){return playerandrank;}
	public static HashMap<UUID, HashMap<String, Integer>> getHashMap(){return hashmap;}
	public static void addPoint(UUID uuid, String obj, int point) {
		
		if(getHashMap().get(uuid) == null) {
			HashMap<String, Integer> seconderHashMap = new HashMap<>();
			seconderHashMap.put(obj, point);
			getHashMap().put(uuid, seconderHashMap);
			return;
		}
		
		getValue(uuid).put(obj, getValue(uuid).getOrDefault(obj, 0) + point);
	}
	public static int getPoint(UUID uuid, String obj) { return getValue(uuid) == null ? 0 : getValue(uuid).getOrDefault(obj, 0); }
	public static void setRank(Player player, int id) { getPlayerAndRank().put(player.getUniqueId(), id); }
	public static int getRankID(Player player){ return getPlayerAndRank().getOrDefault(getUUID(player.getName()), -1);}
	public static int getRankID(UUID uuid){ return getPlayerAndRank().getOrDefault(uuid, 0);}
	public static int getRankID(String rank) {
		int x = 0;
		for(String ranks : getRanks()) {
			if(ranks.equals(rank)) return x;
			x++;
		}
		return 0;
	}
	public static String getRank(int id) {
		try {
			return ranks[id];
		}catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	public static String getRank(UUID uuid){
		if(getPlayerAndRank() == null) return null;
		return getRanks()[getPlayerAndRank().getOrDefault(uuid, 0)];
	}
	public static String[] getRanks() { return ranks; }
	public static HashMap<String, Integer> getValue(UUID uuid) { return getHashMap().get(uuid); }
	public static boolean requirementsComplete(UUID uuid) {
		if(Main.getInstance().getConfig().getConfigurationSection("ranks." + getRank(uuid)) == null) return false;
		if(getOnlineTime(uuid) < getOnlineTime(getRankID(uuid))) return false;
		for(String node : Main.getInstance().getConfig().getConfigurationSection("ranks." + getRank(uuid) + ".missions").getKeys(false)) {
			if(node.equals("ONLINE_TIME")) continue;
			int requirement = Main.getInstance().getConfig().getInt("ranks." + getRank(uuid) + ".missions." + node);
			int current = getPoint(uuid, node);
			if(requirement > current) return false;
		}
		return true;
	}
	public static UUID getUUID(String name) {		
		if(Bukkit.getOnlineMode()) {
		    String uuid = "";
		    try {
		        Reader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream()));
		        uuid = (((JsonObject)new JsonParser().parse(in)).get("id")).toString().replaceAll("\"", "");
		        uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
		        in.close();
		    } catch (Exception e) {
		        System.out.println("Unable to get UUID of: " + name + "!");
		        uuid = null;
		    }
		    return UUID.fromString(uuid);
		} else {
			@SuppressWarnings("deprecation")
			OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(name);
			if(!offlineplayer.hasPlayedBefore()) {
				return null;
			}
			return offlineplayer.getUniqueId();
		}
	}
	
	public static int getOnlineTime(int rankid) { return Main.getInstance().getConfig().getInt("ranks." + getRanks()[rankid] + ".missions.ONLINE_TIME");}
	public static int getOnlineTime(UUID uuid) {	
		if(Bukkit.getOfflinePlayer(uuid).isOnline()) {
			return (int) Bukkit.getPlayer(uuid).getPlayerTime()/20;
		} else {
			return offlineplayertime.getOrDefault(uuid, 0);
		}
	}
		
	public static HashMap<UUID, Integer> getOfflinePlayerTime() { return offlineplayertime; }
	
	public static String getOnlineTimeToString(UUID uuid) {
		
		//player's online time
		int mil = getOnlineTime(uuid);
		int sec = Math.floorMod(mil, 60); mil /= 60;
		int min = Math.floorMod(mil, 60); mil /= 60;
		int hour = Math.floorMod(mil, 24); mil /= 24;
		int day = Math.floorMod(mil, 30); mil /= 30;
		int mouth = Math.floorMod(mil, 12); mil /= 12;

		String value = Main.getInstance().getConfig().getString("date-format")
		.replaceAll("\\{year\\}", mil + "")
		.replaceAll("\\{mouth\\}", mouth + "")
		.replaceAll("\\{day\\}", day + "")
		.replaceAll("\\{hour\\}", hour + "")
		.replaceAll("\\{min\\}", min + "")
		.replaceAll("\\{sec\\}", sec + "");
		
		//requirement's online time
		int rmil = getOnlineTime(getRankID(uuid));
		int rsec = Math.floorMod(rmil, 60); rmil /= 60;
		int rmin = Math.floorMod(rmil, 60); rmil /= 60;
		int rhour = Math.floorMod(rmil, 24); rmil /= 24;
		int rday = Math.floorMod(rmil, 30); rmil /= 30;
		int rmouth = Math.floorMod(rmil, 12); rmil /= 12;

		String requirement = Main.getInstance().getConfig().getString("date-format")
		.replaceAll("\\{year\\}", rmil + "")
		.replaceAll("\\{mouth\\}", rmouth + "")
		.replaceAll("\\{day\\}", rday + "")
		.replaceAll("\\{hour\\}", rhour + "")
		.replaceAll("\\{min\\}", rmin + "")
		.replaceAll("\\{sec\\}", rsec + "");
		
		String result = Main.getInstance().getConfig().getString("onlinetime-format")
		.replaceAll("\\{value\\}", value)
		.replaceAll("\\{requirement\\}", requirement);
		
		result = ChatColor.translateAlternateColorCodes('&', result);
		
		return result;
	}
}
