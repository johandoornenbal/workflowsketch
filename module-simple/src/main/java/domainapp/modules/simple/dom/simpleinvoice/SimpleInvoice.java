package domainapp.modules.simple.dom.simpleinvoice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.clock.ClockService;

import domainapp.modules.simple.SimpleModule;
import domainapp.modules.simple.dom.WithWorkFlow;
import domainapp.modules.simple.dom.WorkFlowState;
import domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow.SimpleInvoiceState;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable()
@DatastoreIdentity(strategy= IdGeneratorStrategy.IDENTITY, column="id")
@Version(strategy= VersionStrategy.DATE_TIME, column="version")
@Queries(
        @Query(
                name = "findByInvoiceNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice "
                        + "WHERE invoiceNumber == :invoiceNumber ")
)
@DomainObject(auditing = Auditing.ENABLED, objectType = "SimpleInvoice")
@DomainObjectLayout()
public class SimpleInvoice implements WithWorkFlow {

    public static class Updated extends SimpleModule.ActionDomainEvent<SimpleInvoice> { }

    @Getter @Setter
    @Column(allowsNull = "false")
    @Title
    private String invoiceNumber;

    @Action(domainEvent = Updated.class)
    public SimpleInvoice initializeState(){
        return this;
    }

    @Action(publishing = Publishing.DISABLED)
    public SimpleInvoice deleteState() {
        setInvoiceState(null);
        return this;
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    private SimpleInvoiceState invoiceState;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(editing = Editing.ENABLED)
    private SimpleInvoiceType invoiceType;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(editing = Editing.ENABLED)
    private BigDecimal amount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate completedOn;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate approvedByManagerOn;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate approvedByCountryLeaderOn;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate paidOn;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate discardedOn;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate rejectedOn;

    @Action(domainEvent = Updated.class)
    public SimpleInvoice complete(@Parameter(optionality = Optionality.OPTIONAL) final Person assignNextTaskTo){
        setCompletedOn(clockService.now());
        // Todo: introduce state REJECTED ?
        // See scenario SimpleInvoiceStateMachine_Test that describes when rejectOn is not set to null
        setRejectedOn(null);
        return this;
    }

    public List<Person> choices0Complete(){
        return Arrays.stream(Person.values()).filter(x->x.hasRoles().contains(Role.MANAGER)).collect(Collectors.toList());
    }

    public String disableComplete(){
        if (getAmount()==null || getInvoiceType()==null){
            return "Amount and type are required to complete";
        }
        return null;
    }

    public boolean hideComplete(){
        return getInvoiceState()!=SimpleInvoiceState.NEW;
    }

    @Action(domainEvent = Updated.class)
    public SimpleInvoice approveAsManager(){
        setApprovedByManagerOn(clockService.now());
        return this;
    }

    public boolean hideApproveAsManager(){
        return getInvoiceState()!=SimpleInvoiceState.COMPLETED;
    }

    @Action(domainEvent = Updated.class)
    public SimpleInvoice approveAsCountryLeader(){
        setApprovedByCountryLeaderOn(clockService.now());
        return this;
    }

    public boolean hideApproveAsCountryLeader(){
        return getInvoiceState()!=SimpleInvoiceState.APPROVED_BY_MANAGER;
    }

    @Action(domainEvent = Updated.class)
    public SimpleInvoice pay(){
        setPaidOn(clockService.now());
        return this;
    }

    public boolean hidePay(){
        return getInvoiceState()!=SimpleInvoiceState.PAYABLE;
    }

    @Action(domainEvent = Updated.class)
    public SimpleInvoice discard(){
        setDiscardedOn(clockService.now());
        return this;
    }

    public boolean hideDiscard(){
        return getInvoiceState()!=SimpleInvoiceState.NEW;
    }

    @Action(domainEvent = Updated.class)
    public SimpleInvoice reject(){
        setRejectedOn(clockService.now());
        setCompletedOn(null);
        return this;
    }

    public boolean hideReject(){
        return getInvoiceState()==null || !Arrays.asList(SimpleInvoiceState.COMPLETED, SimpleInvoiceState.APPROVED_BY_MANAGER, SimpleInvoiceState.PAYABLE).contains(getInvoiceState());
    }

    @Override
    @Programmatic
    public WorkFlowState getWorkFlowState() {
        return getInvoiceState();
    }

    @Override
    @Programmatic
    public void setWorkFlowState(final WorkFlowState workFlowState) {
        this.setInvoiceState((SimpleInvoiceState) workFlowState);
    }

    @Inject
    ClockService clockService;
}
