package io.github.cottonmc.skillworks;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.SyntaxError;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigManager {
	public static <T> T load(Class<T> clazz){
		System.out.println("Loading config!");
		try {
			File file = new File(FabricLoader.getInstance().getConfigDirectory().toString() + "/" + "Skillworks.json5");
			Jankson jankson = Jankson.builder().build();

			//Generate config file if it doesn't exist
			if(!file.exists()) {
				saveDefault(clazz.newInstance());
			}

			try {
				JsonObject json = jankson.load(file);

				return jankson.fromJson(json, clazz);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SyntaxError syntaxError) {
			syntaxError.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveDefault(Object obj) {
		File configFile = new File(FabricLoader.getInstance().getConfigDirectory().toString() + "/" + "Skillworks.json5");
		Jankson jankson = Jankson.builder().build();
		String result = jankson
				.toJson(obj) //The first call makes a JsonObject
				.toJson(true, true, 0);     //The second turns the JsonObject into a String -
		//in this case, preserving comments and pretty-printing with newlines
		try {
			if(!configFile.exists()) configFile.createNewFile();
			FileOutputStream out = new FileOutputStream(configFile, false);

			out.write(result.getBytes());
			out.flush();
			out.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
