package domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice;

@Mixin
public class SimpleInvoice_tasks {

    private final SimpleInvoice invoice;

    public SimpleInvoice_tasks(SimpleInvoice invoice) {
        this.invoice = invoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<SimpleInvoiceTask> $$() {
        return simpleInvoiceTaskRepository.findByInvoice(invoice);
    }

    @Inject
    SimpleInvoiceTaskRepository simpleInvoiceTaskRepository;

}
