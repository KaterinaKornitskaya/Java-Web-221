package itstep.learning.ioc;

import com.google.inject.AbstractModule;
import itstep.learning.services.datetime.DatetimeService;
import itstep.learning.services.datetime.UtilDatetimeService;
import itstep.learning.services.random.RandomService;
import itstep.learning.services.random.UtilRandomService;

public class SeviceConfig extends AbstractModule {

    // Alt+insert -> override methods -> configure
    @Override
    protected void configure() {
        bind(RandomService.class).to(UtilRandomService.class);
        // інструкція вище аналогічна AddSingletone<IRandomService, UtilRandomService>()
        // тут ми зв'язуємо інтерфейс RandomService з класом RandomService
        // потім цей SeviceConfig треба додати до listener в IocContextListener

        bind(DatetimeService.class).to(UtilDatetimeService.class);
    }
}
