package itstep.learning.services.random;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.datetime.DatetimeService;

import java.util.Random;

@Singleton
public class UtilRandomService implements RandomService {

    private final DatetimeService datetimeService;
    private Random random;

    @Inject
    public UtilRandomService(DatetimeService datetimeService) {
        this.datetimeService = datetimeService;
    }

    @Override
    public int randomInt() {
        long seed = datetimeService.getCurrentTimestamp();
        random = new Random(seed);
        return random.nextInt();
    }

    @Override
    public String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));  // charAt(index) возвращает символ, который находится по заданному индексу.
        }
        return sb.toString();
    }

    @Override
    public String randomFileName(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
