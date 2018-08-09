package domainapp.modules.simple.dom.impl.simpleworkflow;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.modules.simple.dom.WithWorkFlow;
import domainapp.modules.simple.dom.WorkFlowActor;
import domainapp.modules.simple.dom.WorkFlowTaskService;
import domainapp.modules.simple.dom.impl.SimpleObject;

@DomainService(nature = NatureOfService.DOMAIN)
public class SimpleTaskService implements WorkFlowTaskService{


    @Override
    public void maintainTasksFor(final WithWorkFlow domainEntity, final WorkFlowActor actor) {
        if (domainEntity.getWorkFlowState().hasTaskFor()!=null) {
            simpleTaskRepository.findOrCreate((SimpleObject) domainEntity);
        }
        cleanUpTasksFor(domainEntity);
    }

    private void cleanUpTasksFor(final WithWorkFlow domainObject) {
        List<SimpleTask> tasksForObj = simpleTaskRepository.findByObject((SimpleObject) domainObject);
        for (SimpleTask task : tasksForObj){
            SimpleObject castedObj = (SimpleObject) domainObject;
            if (castedObj.getState()!=null && !task.applicableForStates().contains(castedObj.getState())){
                repositoryService.removeAndFlush(task);
            }
        }
    }

    @Inject
    SimpleTaskRepository simpleTaskRepository;

    @Inject
    RepositoryService repositoryService;

}
