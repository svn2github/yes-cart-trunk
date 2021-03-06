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

package org.yes.cart.web.service.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.ro.NodeRO;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.ws.node.NodeService;
import org.yes.cart.web.service.ws.node.dto.Node;

/**
 * User: denispavlov
 * Date: 19/08/2014
 * Time: 21:45
 */
@Controller
@RequestMapping("/node")
public class NodeController extends AbstractApiController {

    @Autowired
    private NodeService nodeService;


    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody NodeRO index() {

        final Node node = nodeService.getCurrentNode();

        final NodeRO nodeRO = new NodeRO();
        nodeRO.setNodeId(node.getNodeId());
        nodeRO.setShopCode(ShopCodeContext.getShopCode());
        return nodeRO;
    }

}
