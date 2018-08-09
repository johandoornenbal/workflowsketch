/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package domainapp.modules.simple.fixture;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.fixturescripts.setup.PersonaEnumPersistAll;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoice;
import domainapp.modules.simple.dom.simpleinvoice.SimpleInvoiceRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SimpleInvoice_persona implements PersonaWithBuilderScript<SimpleInvoice, SimpleInvoiceBuilder>,
        PersonaWithFinder<SimpleInvoice> {

    I123("I-123"),
    I234("I-234"),
    I345("I-345"),
    I456("I-456"),
    I567("I-567"),
    I678("I-678"),
    I789("I-789");

    private final String invoiceNumber;

//    @Override
    public SimpleInvoiceBuilder builder() {
        return new SimpleInvoiceBuilder().setInvoiceNumber(invoiceNumber);
    }

    //@Override
    public SimpleInvoice findUsing(final ServiceRegistry2 serviceRegistry) {
        SimpleInvoiceRepository simpleInvoices = serviceRegistry.lookupService(SimpleInvoiceRepository.class);
        return simpleInvoices.findByInvoiceNumber(invoiceNumber);
    }

    public static class PersistAll
            extends PersonaEnumPersistAll<SimpleInvoice_persona, SimpleInvoice, SimpleInvoiceBuilder> {
        public PersistAll() {
            super(SimpleInvoice_persona.class);
        }
    }
}
