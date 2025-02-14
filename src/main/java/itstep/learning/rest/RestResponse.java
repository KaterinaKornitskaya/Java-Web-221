package itstep.learning.rest;

import java.util.Map;

// клас що буде визначати інтерфейс
public class RestResponse {
    private int status;

    private String message;

    // Resource identification in requests: Individual resources are identified in requests using URIs.
    private String resourceUrl;

    // key-value
    private Map<String, String> meta;

    private long cacheTime;  // скільки секунд валідний кеш
    // тобто скільки секунд можна цей ресурс не оновлювати, тобто кешувати
    // є ресурси, які валідні декілька хвилин, а є - місяць

    // дані
    private Object data;
    // взагалі нам потрібні тільки це останнє поле - дані,
    // але за рекомендаціями до побудови РЕСТ ми створили також і всі попередні поля

    public int getStatus() {
        return status;
    }

    public RestResponse setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RestResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public RestResponse setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
        return this;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public RestResponse setMeta(Map<String, String> meta) {
        this.meta = meta;
        return this;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public RestResponse setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        return this;
    }

    public Object getData() {
        return data;
    }

    public RestResponse setData(Object data) {
        this.data = data;
        return this;
    }
}
