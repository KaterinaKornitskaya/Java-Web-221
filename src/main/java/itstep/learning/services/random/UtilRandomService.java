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
}
