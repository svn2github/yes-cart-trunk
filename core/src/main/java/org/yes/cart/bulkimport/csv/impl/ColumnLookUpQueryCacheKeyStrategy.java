/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.bulkimport.csv.impl;

import org.yes.cart.bulkimport.model.ImportColumn;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.bulkimport.model.ValueAdapter;
import org.yes.cart.bulkimport.service.support.EntityCacheKeyStrategy;
import org.yes.cart.bulkimport.service.support.LookUpQuery;
import org.yes.cart.bulkimport.service.support.LookUpQueryParameterStrategy;
import org.yes.cart.domain.entity.Identifiable;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 10:40 AM
 */
public class ColumnLookUpQueryCacheKeyStrategy implements EntityCacheKeyStrategy {

    private final LookUpQueryParameterStrategy hsqlStrategy;

    public ColumnLookUpQueryCacheKeyStrategy(final LookUpQueryParameterStrategy hsqlStrategy) {
        this.hsqlStrategy = hsqlStrategy;
    }

    /** {@inheritDoc} */
    public String keyFor(final ImportDescriptor descriptor,
                         final ImportColumn column,
                         final Object masterObject,
                         final ImportTuple tuple,
                         final ValueAdapter adapter) {

        final LookUpQuery query = hsqlStrategy.getQuery(descriptor, masterObject, tuple, adapter, column.getLookupQuery());

        final StringBuilder sb = new StringBuilder();
        sb.append(column.getName()).append('_').append(column.getColumnIndex()).append('_').append(query.getQueryString());
        for (Object obj : query.getParameters()) {
            sb.append('_').append(obj);
        }

        if (column.isUseMasterObject()) {
            if (masterObject instanceof Identifiable) {
                sb.append('_').append(((Identifiable) masterObject).getId());
            } else {
                sb.append("_unknownMaster");
            }
        }
        return sb.toString();
    }
}
