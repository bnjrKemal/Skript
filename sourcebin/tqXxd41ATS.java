		for(String node : Main.getInstance().getConfig().getConfigurationSection("ranks").getKeys(false)) {
			int slot = Main.getInstance().getConfig().getInt(node + ".item.slot");
			String type = Main.getInstance().getConfig().getString(node + ".item.type");
			String name = Main.getInstance().getConfig().getString(node + ".item.name");
			List<String> lore = Main.getInstance().getConfig().getStringList(node + ".item.lore");
			
			ItemStack item = new ItemStack(Material.getMaterial(type));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			meta.setLore(lore);
			item.setItemMeta(meta);
			
			linked.put(slot, item);
		}
