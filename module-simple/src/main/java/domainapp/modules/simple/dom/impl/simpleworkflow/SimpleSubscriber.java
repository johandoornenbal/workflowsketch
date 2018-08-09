package domainapp.modules.simple.dom.impl.simpleworkflow;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.modules.simple.dom.impl.SimpleObject;

@DomainService(nature = NatureOfService.DOMAIN)
public class SimpleSubscriber extends AbstractSubscriber {

    @Subscribe
    @EventHandler
    public void onNameUpdatedEvent(final SimpleObject.NameUpdated e){
        if (e.getEventPhase().isExecuted()) {
            simpleWorkFlowStateMachine.transition(e.getSource());
            simpleTaskService.maintainTasksFor(e.getSource(), null);
        }
    }

    @Subscribe
    @EventHandler
    public void onDeleteEvent(final SimpleObject.DeleteEvent e){
        if (e.getEventPhase().isExecutingOrLater()) {
            simpleTaskRepository.findByObject(e.getSource()).forEach(
                    x->{
                        repositoryService.removeAndFlush(x);
                    }
            );
        }
    }

    @Inject
    SimpleWorkFlowStateMachine simpleWorkFlowStateMachine;

    @Inject
    SimpleTaskService simpleTaskService;

    @Inject
    SimpleTaskRepository simpleTaskRepository;

    @Inject
    RepositoryService repositoryService;

}
