package domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.modules.simple.dom.simpleinvoice.Person;
import domainapp.modules.simple.dom.simpleinvoice.Role;
import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = SimpleInvoiceTask.class,
        objectType = "simple.SimpleInvoiceTaskRepository"
)
public class SimpleInvoiceTaskRepository {

    @Programmatic
    public List<SimpleInvoiceTask> listAll() {
        return repositoryService.allInstances(SimpleInvoiceTask.class);
    }

    @Programmatic
    public List<SimpleInvoiceTask> findByInvoice(
            final SimpleInvoice invoice
    ) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        SimpleInvoiceTask.class,
                        "findByInvoice",
                        "invoice", invoice));
    }


    @Programmatic
    public List<SimpleInvoiceTask> findByInvoiceAndRole(
            final SimpleInvoice invoice,
            final Role role
    ) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        SimpleInvoiceTask.class,
                        "findByInvoiceAndRole",
                        "invoice", invoice, "role", role));
    }


    @Programmatic
    public SimpleInvoiceTask create(final SimpleInvoice invoice, final Role role) {
        final SimpleInvoiceTask simpleInvoiceTask = new SimpleInvoiceTask();
        serviceRegistry2.injectServicesInto(simpleInvoiceTask);
        simpleInvoiceTask.setInvoice(invoice);
        simpleInvoiceTask.setAssignedToRole(role);
        repositoryService.persistAndFlush(simpleInvoiceTask);
        return simpleInvoiceTask;
    }

    @Programmatic
    public SimpleInvoiceTask findOrCreate(
            final SimpleInvoice invoice,
            final Role role
    ) {
        List<SimpleInvoiceTask> simpleTasks = findByInvoiceAndRole(invoice, role);
        SimpleInvoiceTask simpleTask;
        if (simpleTasks.isEmpty()) {
            simpleTask = create(invoice, role);
        } else {
            simpleTask = simpleTasks.get(0);
        }
        return simpleTask;
    }

    @Programmatic
    public SimpleInvoiceTask findOrCreate(
            final SimpleInvoice invoice,
            final Role role,
            final Person person
    ) {
        SimpleInvoiceTask simpleTask = this.findOrCreate(invoice, role);
        simpleTask.setAssignedTo(person);
        return simpleTask;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;
}
