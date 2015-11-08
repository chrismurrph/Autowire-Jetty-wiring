package wiring

import com.cmts.server.business.{SmartgasUtils, PropertiesHelper, SmartgasHome}
import com.cmts.server.store.SmartgasServerDatastoreFactory
import com.seasoft.alarmer.common.domain.{DomainSession, DomainFactoryI}
import com.seasoft.alarmer.server.store.OpcTypes
import org.springframework.beans.factory.ListableBeanFactory

/**
 * User: Chris
 * Date: 07/07/2015
 * Time: 5:36 PM
 *
 * Straight copy of a file in atmos-ser
 *
 */
class SmartgasInfo {
  private val OPCTypes: OpcTypes = new OpcTypes
  private val DomainFactory: DomainFactoryI = DomainSession.getDomainFactoryInstance
  private val res: SmartgasHome.SmartgasHomeStrRes = SmartgasHome.createSmartgasHomeStr
  private val propertiesHelper = new PropertiesHelper(res.homeStr, PropertiesHelper.saneDirectoryName(SmartgasHome.getEtcDirectory), OPCTypes, DomainFactory, res.onUnix)
  val dsFactory: SmartgasServerDatastoreFactory = SmartgasServerDatastoreFactory.getNonApplicationInstance
  val beanFactory: ListableBeanFactory = SmartgasUtils.getBeanFactory(etcDir())
  println(s"beanFactory are going to use is $beanFactory")

  private def etcDir(): String = {
    propertiesHelper.loadProperties()
    propertiesHelper.getHomes.etc
  }
}
