package net.bnjrKemal.MissionV4;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class Data {
	
	private static HashMap<UUID, HashMap<Object, Integer>> hashmap = new HashMap<>();
	
	private static File file;
	private static YamlConfiguration yaml;
	
	public Data() {
		file = new File(Bukkit.getPluginManager().getPlugin("MissionV4").getDataFolder(), "data.yml");
		if(!file.exists()) {
			Bukkit.getPluginManager().getPlugin("MissionV4").getDataFolder().mkdirs();
			try {file.createNewFile();}catch(IOException e){e.printStackTrace();}
		}
		yaml = YamlConfiguration.loadConfiguration(file);

		HashMap<Object, Integer> seconderHashMap;
		UUID uuid;
		for(String suuid : getYAML().getConfigurationSection("data").getKeys(false)) {
			uuid = UUID.fromString(suuid);
			seconderHashMap = new HashMap<>();
			for(String key : getYAML().getConfigurationSection("data." + uuid).getKeys(false)) {
				int value = getYAML().getConfigurationSection("data." + uuid).getInt(key);
				seconderHashMap.put(key, value);
			}
			hashmap.put(uuid, seconderHashMap);
		}
		
	}
	
	public static YamlConfiguration getYAML() { return yaml;}
	
	public static File getFile() { return file; }
	
	public static void addPoint(UUID uuid, Object obj, int point) {

		HashMap<Object, Integer> seconderHashMap = new HashMap<>();
		
		if(hashmap.get(uuid) == null) {
			seconderHashMap.put(obj, point);
			hashmap.put(uuid, seconderHashMap);
			return;
		}
		
		HashMap<Object, Integer> value = hashmap.get(uuid);
		if(value.get(obj) == null) {
			value.put(obj, point);
			return;
		}
		value.put(obj, value.get(obj) + point);
	}
	public static int getPoint(UUID uuid, Object obj) {	
		HashMap<Object, Integer> value = hashmap.get(uuid);
		if(value == null) return 0;
		return value.get(obj);
	}
	
	public static HashMap<UUID, HashMap<Object, Integer>> getHashMap(){return hashmap;}
}
