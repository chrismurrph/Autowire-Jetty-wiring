package wiring

import com.cmts.server.business.SmartgasUtils
import com.seasoft.alarmer.common.domain.DomainSession
import com.seasoft.common.store.ServerDataStoreFactoryI
import com.seasoft.common.utils.Err
import com.seasoft.common.view.{LoginAction, SpringLoginAction}
import org.springframework.beans.factory.ListableBeanFactory

/**
 * User: Chris
 * Date: 03/07/2015
 * Time: 3:50 PM
 */
class UsersConnecting(info:SmartgasInfo) {
  val OperatorUsername = "WebUser"
  val OperatorPassword = "NeedsThrottled"

  private def authenticateDataStoreFactory (dsFactory: ServerDataStoreFactoryI, beanFactory: ListableBeanFactory):LoginAction = {
    val loginAction: LoginAction = new SpringLoginAction(beanFactory, dsFactory.getSecureConnectionEnum, dsFactory, false)
    try {
      loginAction.logonToServer(OperatorUsername, OperatorPassword, null, DomainSession.getRoleEnumFactoryInstance.getRoleEnumCRO, SmartgasUtils.USER_DETAILS_FACTORY)
    }
    catch {
      case e: Exception =>
        Err.pr(s"WARNING: User not authenticated: $OperatorUsername, b/c: ${e.getMessage}")
        loginAction.setException(e)
    }
    loginAction
  }

  def connect():LoginAction = {
    val loginAction = authenticateDataStoreFactory(info.dsFactory, info.beanFactory)
    println("---------------------->> Logged in and got " + loginAction)
    loginAction
  }
}
