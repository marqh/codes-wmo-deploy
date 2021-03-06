/******************************************************************
 * File:        JsonContext.java
 * Created by:  Dave Reynolds
 * Created on:  25 Feb 2013
 * 
 * (c) Copyright 2013, Epimorphics Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *****************************************************************/

package com.epimorphics.registry.webapi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.epimorphics.registry.util.JSONLDSupport;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@Path(JsonContext.JSON_CONTEXT_PATH)
public class JsonContext {
    public static final String JSON_CONTEXT_PATH = "/system/json-context";
    
    @GET
    @Produces(JSONLDSupport.MIME_JSONLD)
    public Model serveDefaultJsonldContext() {
        return ModelFactory.createDefaultModel();
    }

}
