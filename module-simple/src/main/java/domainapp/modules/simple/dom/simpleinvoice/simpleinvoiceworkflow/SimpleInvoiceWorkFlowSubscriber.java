package domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import domainapp.modules.simple.dom.simpleinvoice.Person;
import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice;

@DomainService(nature = NatureOfService.DOMAIN, objectType = "simpleinvoice.simpleinvoiceworkflow.SimpleInvoiceWorkFlowSubscriber")
public class SimpleInvoiceWorkFlowSubscriber extends AbstractSubscriber {

    @Subscribe
    public void onUpdatedEvent(final SimpleInvoice.Updated e){
        if (e.getEventPhase().isExecuted()) {
            simpleInvoiceStateMachine.transition(e.getSource());
            Person actor = determineActorIfAny(e);
            simpleInvoiceTaskService.maintainTasksFor(e.getSource(), actor);
        }
    }


    private Person determineActorIfAny(final SimpleInvoice.Updated e){
        if (!e.getArguments().isEmpty() && e.getArguments().get(0)!=null && e.getArguments().get(0).getClass().getTypeName().contains("domainapp.modules.simple.dom.simpleinvoice.Person")) {
            return (Person) e.getArguments().get(0);
        }
        else {
            return null;
        }
    }




    @Inject
    SimpleInvoiceWorkFlowStateMachine simpleInvoiceStateMachine;

    @Inject
    SimpleInvoiceTaskService simpleInvoiceTaskService;

}
