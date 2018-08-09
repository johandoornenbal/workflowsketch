package domainapp.modules.simple.dom.impl.simpleworkflow;

import java.util.Arrays;
import java.util.List;

import domainapp.modules.simple.dom.Guard;
import domainapp.modules.simple.dom.TargetAndGuard;
import domainapp.modules.simple.dom.WithWorkFlow;
import domainapp.modules.simple.dom.WorkFlowRole;
import domainapp.modules.simple.dom.WorkFlowState;
import domainapp.modules.simple.dom.impl.SimpleObject;
import lombok.AllArgsConstructor;

public enum SimpleState implements WorkFlowState {

    INIT {
        @Override
        public List<TargetAndGuard> canTransitionTo() {
            return Arrays.asList(
                    new SimpleTargetAndGuard(NEW, SimpleGuard.ALWAYS)
            );
        }

        @Override
        public WorkFlowRole hasTaskFor() {
            return null;
        }
    },
    NEW{
        @Override
        public List<TargetAndGuard> canTransitionTo() {
            return Arrays.asList(
                    new SimpleTargetAndGuard(COMPLETED, SimpleGuard.NEW_COMPLETED),
                    new SimpleTargetAndGuard(DISCARDED, SimpleGuard.DISCARD)
            );
        }
        @Override
        public WorkFlowRole hasTaskFor() {
            return SimpleRole.A_SIMPLE_ROLE;
        }
    },
    COMPLETED{
        @Override
        public List<TargetAndGuard> canTransitionTo() {
            return Arrays.asList();
        }
        @Override
        public WorkFlowRole hasTaskFor() {
            return null;
        }
    },
    DISCARDED{
        @Override
        public List<TargetAndGuard> canTransitionTo() {
            return Arrays.asList(
                    new SimpleTargetAndGuard(NEW, SimpleGuard.DISCARDED_NEW)
            );
        }
        @Override
        public WorkFlowRole hasTaskFor() {
            return null;
        }
    }
    ;

    enum SimpleGuard implements Guard {

        ALWAYS {
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                return true;
            }
        },
        NEW_COMPLETED {
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                SimpleObject castedObject = (SimpleObject) domainEntity;
                return castedObject.getName().length()>10;
            }
        },
        DISCARDED_NEW {
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                SimpleObject castedObject = (SimpleObject) domainEntity;
                return castedObject.getNotes()!=null && castedObject.getNotes().contains("revive") && !castedObject.getNotes().contains("discard");
            }
        },
        DISCARD {
            @Override
            public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                SimpleObject castedObject = (SimpleObject) domainEntity;
                return castedObject.getNotes()!=null && castedObject.getNotes().contains("discard");
            }
        };

        public abstract boolean isSatisfiedFor(final WithWorkFlow domainEntity);

    }

    @AllArgsConstructor
    class SimpleTargetAndGuard implements TargetAndGuard {

        SimpleState target;
        SimpleGuard guard;

        @Override
        public WorkFlowState getTarget() {
            return target;
        }

        @Override
        public Guard getGuard() {
            return guard;
        }
    }

}
