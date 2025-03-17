package itstep.learning.ioc;

import com.google.inject.AbstractModule;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.services.config.ConfigService;
import itstep.learning.services.config.JsonConfigService;
import itstep.learning.services.datetime.DatetimeService;
import itstep.learning.services.datetime.UtilDatetimeService;
import itstep.learning.services.db.DbService;
import itstep.learning.services.db.MySqlDbService;
import itstep.learning.services.hash.HashService;
import itstep.learning.services.hash.Md5HashService;
import itstep.learning.services.kdf.KdfService;
import itstep.learning.services.kdf.PbKdf1Service;
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
        bind(HashService.class).to(Md5HashService.class);
        bind(KdfService.class).to(PbKdf1Service.class);
        bind(DbService.class).to(MySqlDbService.class);
        bind(ConfigService.class).to(JsonConfigService.class);
        // bind(DataContext)
    }
}
