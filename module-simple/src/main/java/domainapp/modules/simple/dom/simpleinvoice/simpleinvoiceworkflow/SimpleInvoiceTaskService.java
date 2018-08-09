package domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.modules.simple.dom.WithWorkFlow;
import domainapp.modules.simple.dom.WorkFlowActor;
import domainapp.modules.simple.dom.WorkFlowTaskService;
import domainapp.modules.simple.dom.simpleinvoice.Person;
import domainapp.modules.simple.dom.simpleinvoice.Role;
import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice;

@DomainService(nature = NatureOfService.DOMAIN)
public class SimpleInvoiceTaskService implements WorkFlowTaskService{

    @Override
    public void maintainTasksFor(final WithWorkFlow domainEntity, final WorkFlowActor actor) {
        maintainTasksFor((SimpleInvoice) domainEntity, (Person) actor);
    }

    private void maintainTasksFor(final SimpleInvoice invoice, final Person actor){
        if (invoice.getWorkFlowState().hasTaskFor()!=null) {
            if (actor !=null){
                simpleInvoiceTaskRepository.findOrCreate(invoice, (Role) invoice.getInvoiceState().hasTaskFor(), actor);
            }
            else {
                simpleInvoiceTaskRepository.findOrCreate(invoice, (Role) invoice.getInvoiceState().hasTaskFor());
            }
        }
        cleanUpTasksFor(invoice);
    }

    private void cleanUpTasksFor(final SimpleInvoice invoice) {
        List<SimpleInvoiceTask> tasksForObj = simpleInvoiceTaskRepository.findByInvoice(invoice);
        for (SimpleInvoiceTask task : tasksForObj){
            if (invoice.getInvoiceState()!=null &&
                    (!task.applicableForStates().contains(invoice.getInvoiceState())
                    || (invoice.getInvoiceState().hasTaskFor()!=null && invoice.getInvoiceState().hasTaskFor()!=task.getAssignedToRole()))
                    ){
                repositoryService.removeAndFlush(task);
            }
        }
    }

    @Inject
    SimpleInvoiceTaskRepository simpleInvoiceTaskRepository;

    @Inject
    RepositoryService repositoryService;

}
