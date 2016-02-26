/*******************************************************************************
 * Copyright 2015 InfinitiesSoft Solutions Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.infinities.skyport.ui;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.ContextRequirements;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.admin.AdminServices;
import org.dasein.cloud.ci.CIServices;
import org.dasein.cloud.identity.IdentityServices;
import org.dasein.cloud.platform.PlatformServices;
import org.dasein.cloud.storage.StorageServices;
import org.dasein.cloud.util.NamingConstraints;
import org.dasein.cloud.util.ResourceNamespace;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;

import com.infinities.skyport.ServiceProvider;
import com.infinities.skyport.compute.SkyportComputeServices;
import com.infinities.skyport.dc.SkyportDataCenterServices;
import com.infinities.skyport.network.SkyportNetworkServices;

public class NoExtraValueServiceProvider implements ServiceProvider {

	protected Mockery context = new JUnit4Mockery() {

		{
			setThreadingPolicy(new Synchroniser());
		}
	};

	private SkyportDataCenterServices dataCenterServices = null;


	public NoExtraValueServiceProvider() {
		dataCenterServices = context.mock(SkyportDataCenterServices.class);

	}

	@Override
	public void initialize() {

	}

	@Override
	public String testContext() {

		return null;
	}

	@Override
	public boolean isConnected() {

		return false;
	}

	@Override
	public boolean hasAdminServices() {

		return false;
	}

	@Override
	public boolean hasCIServices() {

		return false;
	}

	@Override
	public boolean hasComputeServices() {

		return false;
	}

	@Override
	public boolean hasIdentityServices() {

		return false;
	}

	@Override
	public boolean hasNetworkServices() {

		return false;
	}

	@Override
	public boolean hasPlatformServices() {

		return false;
	}

	@Override
	public boolean hasStorageServices() {

		return false;
	}

	@Override
	public StorageServices getStorageServices() {

		return null;
	}

	@Override
	public AdminServices getAdminServices() {

		return null;
	}

	@Override
	public CloudProvider getComputeCloud() {

		return null;
	}

	@Override
	public ProviderContext getContext() {

		return null;
	}

	@Override
	public ContextRequirements getContextRequirements() {
		return new ContextRequirements();
	}

	@Override
	public String getCloudName() {
		return "mock";
	}

	@Override
	public SkyportDataCenterServices getSkyportDataCenterServices() {
		return dataCenterServices;
	}

	@Override
	public CIServices getCIServices() {

		return null;
	}

	@Override
	public SkyportComputeServices getSkyportComputeServices() {
		return null;
	}

	@Override
	public IdentityServices getIdentityServices() {

		return null;
	}

	@Override
	public SkyportNetworkServices getSkyportNetworkServices() {

		return null;
	}

	@Override
	public PlatformServices getPlatformServices() {
		return null;
	}

	@Override
	public String getProviderName() {
		return "mock";
	}

	@Override
	public String findUniqueName(String baseName, NamingConstraints constraints, ResourceNamespace namespace)
			throws CloudException, InternalException {
		return null;
	}

	@Override
	public void close() {

	}

}
