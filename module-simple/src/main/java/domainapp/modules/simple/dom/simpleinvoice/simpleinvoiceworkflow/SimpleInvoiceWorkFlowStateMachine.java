package domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import domainapp.modules.simple.dom.WorkFlowStateMachineAbstract;
import domainapp.modules.simple.dom.WithWorkFlow;

@DomainService(nature = NatureOfService.DOMAIN)
public class SimpleInvoiceWorkFlowStateMachine extends WorkFlowStateMachineAbstract {

    @Override
    public WithWorkFlow initialise(WithWorkFlow domainEntity) {
        domainEntity.setWorkFlowState(SimpleInvoiceState.INIT);
        return domainEntity;
    }

}
