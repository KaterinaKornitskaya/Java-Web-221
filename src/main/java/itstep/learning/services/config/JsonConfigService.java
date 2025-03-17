package itstep.learning.services.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class JsonConfigService implements ConfigService {

    private final Logger logger;

    // головний елемент, який бкудемо зчитувати і розпаковувати
    private JsonElement configElement;

    @Inject
    public JsonConfigService(Logger logger) {
        this.logger = logger;
    }

    @Override
    public JsonPrimitive getValue(String path) {  // path db.MySql.host
        if(configElement == null) {
            init();
        }
        String[] parts = path.split("\\.");
        JsonObject obj = configElement.getAsJsonObject();
        for (int i = 0; i < parts.length - 1; i++) {
            JsonElement je = obj.get(parts[i]);
            if(je == null) {
                throw new IllegalStateException(
                        "Part of path '" + parts[i] + "' not found");
            }
            obj = je.getAsJsonObject();
        }
        return obj.getAsJsonPrimitive(parts[parts.length - 1]);
//        configElement.getAsJsonObject()
//                .get("token").getAsJsonObject()
//                .get("lifetime").getAsInt();
    }

    private void init(){
        try (InputStream stream = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream("appsettings.json");
             Reader reader = new InputStreamReader(stream)
        ) {
            Gson gson = new Gson();
            configElement = gson.fromJson(reader, JsonElement.class);
        }
        catch (IOException ex){
            logger.log(Level.SEVERE, "JsonConfigService::init", ex);
        }
    }
}
