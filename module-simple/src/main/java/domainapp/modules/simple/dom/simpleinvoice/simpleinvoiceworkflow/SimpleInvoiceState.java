package domainapp.modules.simple.dom.simpleinvoice.simpleinvoiceworkflow;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import domainapp.modules.simple.dom.Guard;
import domainapp.modules.simple.dom.TargetAndGuard;
import domainapp.modules.simple.dom.WithWorkFlow;
import domainapp.modules.simple.dom.WorkFlowRole;
import domainapp.modules.simple.dom.WorkFlowState;
import domainapp.modules.simple.dom.simpleinvoice.Role;
import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice;
import lombok.AllArgsConstructor;

public enum SimpleInvoiceState implements WorkFlowState {

    INIT {
        @Override
        public List<TargetAndGuard> canTransitionTo() {
            return Arrays.asList(
                    new SimpleInvoiceTargetAndGuard(NEW, SimpleInvoiceGuard.ALWAYS)
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
                    new SimpleInvoiceTargetAndGuard(COMPLETED, SimpleInvoiceGuard.NEW_COMPLETED),
                    new SimpleInvoiceTargetAndGuard(DISCARDED, SimpleInvoiceGuard.DISCARD)
            );
        }
        @Override
        public WorkFlowRole hasTaskFor() {
            return Role.ADMINISTRATOR;
        }
    },
    COMPLETED{
        @Override
        public List<TargetAndGuard> canTransitionTo() {
            return Arrays.asList(
                    new SimpleInvoiceTargetAndGuard(NEW, SimpleInvoiceGuard.REJECT),
                    new SimpleInvoiceTargetAndGuard(APPROVED_BY_MANAGER, SimpleInvoiceGuard.COMPLETED_APPBYMAN),
                    new SimpleInvoiceTargetAndGuard(PAYABLE, SimpleInvoiceGuard.COMPLETED_PAYABLE)
            );
        }
        @Override
        public WorkFlowRole hasTaskFor() {
            return Role.MANAGER;
        }
    },
    APPROVED_BY_MANAGER{
        @Override
        public List<TargetAndGuard> canTransitionTo() {
            return Arrays.asList(
                    new SimpleInvoiceTargetAndGuard(PAYABLE, SimpleInvoiceGuard.APPBYMAN_PAYABLE),
                    new SimpleInvoiceTargetAndGuard(NEW, SimpleInvoiceGuard.REJECT)
            );
        }
        @Override
        public WorkFlowRole hasTaskFor() {
            return Role.COUNTRY_LEADER;
        }
    },
    PAYABLE{
        @Override
        public List<TargetAndGuard> canTransitionTo() {
            return Arrays.asList(
                    new SimpleInvoiceTargetAndGuard(PAID, SimpleInvoiceGuard.PAYABLE_PAID),
                    new SimpleInvoiceTargetAndGuard(NEW, SimpleInvoiceGuard.REJECT)
            );
        }
        @Override
        public WorkFlowRole hasTaskFor() {
            return Role.TREASURER;
        }
    },
    PAID{
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
            return Arrays.asList();
        }
        @Override
        public WorkFlowRole hasTaskFor() {
            return null;
        }
    }
    ;

    enum SimpleInvoiceGuard implements Guard {

        ALWAYS {
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                return true;
            }
        },
        NEW_COMPLETED {
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                SimpleInvoice invoice = (SimpleInvoice) domainEntity;
                if (invoice.getCompletedOn()!=null){
                    return true;
                }
                return false;
            }
        },
        COMPLETED_PAYABLE{
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                SimpleInvoice invoice = (SimpleInvoice) domainEntity;
                if (invoice.getRejectedOn()!=null) return false; // redundant, but more explicit
                if (invoice.getApprovedByManagerOn()!=null && invoice.getAmount().compareTo(new BigDecimal("10000.00"))<0){
                    return true;
                }
                return false;
            }

        },
        COMPLETED_APPBYMAN{
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                SimpleInvoice invoice = (SimpleInvoice) domainEntity;
                if (invoice.getRejectedOn()!=null) return false; // redundant, but more explicit
                if (invoice.getApprovedByManagerOn()!=null && invoice.getAmount().compareTo(new BigDecimal("10000.00"))>=0){
                    return true;
                }
                return false;
            }
        },
        APPBYMAN_PAYABLE{
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                SimpleInvoice invoice = (SimpleInvoice) domainEntity;
                if (invoice.getRejectedOn()!=null) return false; // redundant, but more explicit
                if (invoice.getApprovedByCountryLeaderOn()!=null){
                    return true;
                }
                return false;
            }
        },
        PAYABLE_PAID{
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                SimpleInvoice invoice = (SimpleInvoice) domainEntity;
                if (invoice.getRejectedOn()!=null) return false; // redundant, but more explicit
                if (invoice.getPaidOn()!=null){
                    return true;
                }
                return false;
            }

        },
        REJECT {
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                SimpleInvoice invoice = (SimpleInvoice) domainEntity;
                if (invoice.getRejectedOn()!=null){
                    return true;
                }
                return false;
            }
        },
        DISCARD {
            @Override public boolean isSatisfiedFor(final WithWorkFlow domainEntity) {
                SimpleInvoice invoice = (SimpleInvoice) domainEntity;
                if (invoice.getDiscardedOn()!=null){
                    return true;
                }
                return false;
            }
        };

        public abstract boolean isSatisfiedFor(final WithWorkFlow domainEntity);

    }

    @AllArgsConstructor
    class SimpleInvoiceTargetAndGuard implements TargetAndGuard {

        SimpleInvoiceState target;
        SimpleInvoiceGuard guard;

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
