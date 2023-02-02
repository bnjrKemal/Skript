    public static String getUUID(String name) {
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
            return uuid;    
        } else {
            @SuppressWarnings("deprecation")
            OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(name);
            if(offlineplayer.hasPlayedBefore()) {
                return null;
            }
            return offlineplayer.getUniqueId().toString();
        }
    }
