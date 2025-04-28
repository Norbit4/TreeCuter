package pl.norbit.treecuter.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonDataHandler {
    private static final String FILE_PATH = "data.json";

    private JsonDataHandler() {
        throw new IllegalStateException("Utility class");
    }

    public static void saveToJson(List<PlayerData> profiles, String path) {
        try (FileWriter writer = new FileWriter(path + FILE_PATH)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            gson.toJson(profiles, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<PlayerData> loadFromJson(String path) {
        List<PlayerData> dataList = null;

        File file = new File(path + FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(path + FILE_PATH)) {
            Type listType = new TypeToken<List<PlayerData>>() {}.getType();
            Gson gson = new GsonBuilder()
                    .create();
            dataList = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}