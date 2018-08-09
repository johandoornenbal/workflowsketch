package domainapp.modules.simple.dom.impl.simpleworkflow;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import domainapp.modules.simple.dom.WorkFlowStateMachineAbstract;
import domainapp.modules.simple.dom.WithWorkFlow;

@DomainService(nature = NatureOfService.DOMAIN)
public class SimpleWorkFlowStateMachine extends WorkFlowStateMachineAbstract {

    @Override
    public WithWorkFlow initialise(WithWorkFlow domainEntity) {
        domainEntity.setWorkFlowState(SimpleState.INIT);
        return domainEntity;
    }

}
