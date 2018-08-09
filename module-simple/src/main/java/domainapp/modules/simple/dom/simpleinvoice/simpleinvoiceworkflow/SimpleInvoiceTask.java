package domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import domainapp.modules.simple.dom.WithWorkFlow;
import domainapp.modules.simple.dom.WorkFlowActor;
import domainapp.modules.simple.dom.WorkFlowRole;
import domainapp.modules.simple.dom.WorkFlowState;
import domainapp.modules.simple.dom.WorkFlowTask;
import domainapp.modules.simple.dom.simpleinvoice.Person;
import domainapp.modules.simple.dom.simpleinvoice.Role;
import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(identityType= IdentityType.DATASTORE)
@DatastoreIdentity(strategy= IdGeneratorStrategy.IDENTITY, column="id")
@Version(strategy= VersionStrategy.DATE_TIME, column="version")
@DomainObject(objectType = "domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow.SimpleInvoiceTask")
@DomainObjectLayout()
@Queries({
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow.SimpleInvoiceTask "
                        + "WHERE invoice == :invoice "),
        @Query(
                name = "findByInvoiceAndRole", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow.SimpleInvoiceTask "
                        + "WHERE invoice == :invoice && assignedToRole == :role")
})
public class SimpleInvoiceTask implements WorkFlowTask{


    @Getter @Setter
    @Column(allowsNull = "false")
    private SimpleInvoice invoice;

    @Override
    public WithWorkFlow attachedTo() {
        return getInvoice();
    }

    @Override
    public List<WorkFlowState> applicableForStates() {
        return Arrays.asList(SimpleInvoiceState.NEW, SimpleInvoiceState.COMPLETED);
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    public Role assignedToRole;

    @Override public WorkFlowRole assignedToRole() {
        return getAssignedToRole();
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    public Person assignedTo;

    @Override public WorkFlowActor assignedToActor() {
        return getAssignedTo();
    }

    @Action()
    public SimpleInvoice complete(@Parameter(optionality = Optionality.OPTIONAL) final Person assignNextTaskTo) {
        return wrapperFactory.wrap(getInvoice()).complete(assignNextTaskTo);
    }

    public List<Person> choices0Complete(){
        return Arrays.stream(Person.values()).filter(x->x.hasRoles().contains(Role.MANAGER)).collect(Collectors.toList());
    }

    public String disableComplete(){
        return getInvoice().disableComplete();
    }

    public boolean hideComplete(){
        return getInvoice().hideComplete();
    }

    @Action()
    public SimpleInvoice approveAsManager() {
        return wrapperFactory.wrap(getInvoice()).approveAsManager();
    }


    public boolean hideApproveAsManager(){
        return getInvoice().hideApproveAsManager();
    }

    @Inject
    WrapperFactory wrapperFactory;

}
