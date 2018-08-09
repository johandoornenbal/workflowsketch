package domainapp.modules.simple.dom.simpleinvoice;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "simple.SimpleInvoiceMenu",
        repositoryFor = SimpleInvoice.class
)
public class SimpleInvoiceRepository {

    @Action(semantics = SemanticsOf.SAFE)
    public java.util.List<SimpleInvoice> listAll() {
        return repositoryService.allInstances(SimpleInvoice.class);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public SimpleInvoice findByInvoiceNumber(
            final String invoiceNumber
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        SimpleInvoice.class,
                        "findByInvoiceNumber",
                        "invoiceNumber", invoiceNumber));
    }

    @Action()
    public SimpleInvoice create(final String invoiceNumber) {
        final SimpleInvoice simpleInvoice = new SimpleInvoice();
        serviceRegistry2.injectServicesInto(simpleInvoice);
        simpleInvoice.setInvoiceNumber(invoiceNumber);
        repositoryService.persist(simpleInvoice);
        return simpleInvoice;
    }

    @Programmatic
    public SimpleInvoice findOrCreate(
            final String invoiceNumber
    ) {
        SimpleInvoice simpleInvoice = findByInvoiceNumber(invoiceNumber);
        if (simpleInvoice == null) {
            simpleInvoice = create(invoiceNumber);
        }
        return simpleInvoice;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
