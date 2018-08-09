package domainapp.modules.simple.dom.impl.simpleworkflow;

import java.util.Arrays;
import java.util.List;

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
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import domainapp.modules.simple.dom.WithWorkFlow;
import domainapp.modules.simple.dom.WorkFlowActor;
import domainapp.modules.simple.dom.WorkFlowRole;
import domainapp.modules.simple.dom.WorkFlowState;
import domainapp.modules.simple.dom.WorkFlowTask;
import domainapp.modules.simple.dom.impl.SimpleObject;
import domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow.SimpleInvoiceState;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(identityType= IdentityType.DATASTORE, schema = "simple")
@DatastoreIdentity(strategy= IdGeneratorStrategy.IDENTITY, column="id")
@Version(strategy= VersionStrategy.DATE_TIME, column="version")
@DomainObject(auditing = Auditing.ENABLED)
@DomainObjectLayout()
@Queries(
        @Query(
                name = "findByAttached", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.modules.simple.dom.impl.simpleworkflow.SimpleTask "
                        + "WHERE simpleObject == :simpleObject ")
)
public class SimpleTask implements WorkFlowTask{


    @Getter @Setter
    @Column(allowsNull = "false")
    private SimpleObject simpleObject;

    @Override
    public WithWorkFlow attachedTo() {
        return getSimpleObject();
    }

    @Override
    public List<WorkFlowState> applicableForStates() {
        return Arrays.asList(SimpleInvoiceState.NEW);
    }

    @Override public WorkFlowRole assignedToRole() {
        return null;
    }

    @Override public WorkFlowActor assignedToActor() {
        return null;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public void perform(){
        wrapperFactory.wrap(simpleObject).updateName(simpleObject.getName().concat("123456789")); // wrapping is used in order for event NameUpdated to be emitted
    }

    @Inject
    WrapperFactory wrapperFactory;

}
