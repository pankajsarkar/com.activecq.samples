/*
 * Copyright 2012 david.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.activecq.samples.slingservice.impl;

import com.activecq.samples.slingservice.SampleClusterAwareService;
import com.day.cq.jcrclustersupport.ClusterAware;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

import java.util.Map;

@Component(
    label="ActiveCQ Samples - Sample Cluster-aware Service",
    description="Sample implementation of a service.",
    metatype=true,
    immediate=false
)
@Properties ({
    @Property(
        label="Vendor",
        name=Constants.SERVICE_VENDOR,
        value="ActiveCQ",
        propertyPrivate=true
    )
})
@Service
public class SampleClusterAwareServiceImpl implements SampleClusterAwareService, ClusterAware {

    /** OSGi Properties **/

    private static final boolean DEFAULT_ENABLED = false;
    private boolean enabled = DEFAULT_ENABLED;
    @Property(label="Service Enable/Disable",
        description="Enables/Disables the service without nullifying service reference objects. This enable/disabling must be implemented in all public methods of this service.",
        boolValue=DEFAULT_ENABLED)
    private static final String PROP_ENABLED = "prop.enabled";

    /* OSGi Service References */

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /** Fields **/

    private String postfix = "";

    /** Service Methods **/

    @Override
    public String helloWorld() {
        if(!this.enabled) { return null; }

        return "Hello World" + postfix;
    }

    /** ClusterAware Methods **/
    @Override
    public void unbindRepository() {
        postfix = "";
    }

    @Override
    public void bindRepository(String repositoryId, String clusterId, boolean isMaster) {
        if(isMaster) {
            postfix = " from Master!";
        }
    }

    /** OSGi Component Methods **/

    @Activate
    protected void activate(final ComponentContext componentContext) throws Exception {
        configure(componentContext);
    }

    @Deactivate
    protected void deactivate(ComponentContext ctx) {
        this.enabled = false;
    }

    private void configure(final ComponentContext componentContext) {
        final Map<String, String> properties = (Map<String, String>) componentContext.getProperties();

        // Global Service Enabled/Disable Setting
        this.enabled = PropertiesUtil.toBoolean(properties.get(PROP_ENABLED), DEFAULT_ENABLED);

        // Resource resolvers are NOT thread safe,
        // Do not instantiate as Service (Singleton) instance variables!
    }
}