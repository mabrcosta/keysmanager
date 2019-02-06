import com.google.inject.AbstractModule
import com.mabrcosta.keysmanager.access.conf.AccessModule
import com.mabrcosta.keysmanager.core.config.{CoreModule, JdbcPersistenceModule}
import com.mabrcosta.keysmanager.machines.conf.MachinesModule
import com.mabrcosta.keysmanager.users.conf.UsersModule

class Module extends AbstractModule {

  override def configure() = {
    install(new JdbcPersistenceModule)
    install(new CoreModule)
    install(new UsersModule)
    install(new MachinesModule)
    install(new AccessModule)
  }

}
