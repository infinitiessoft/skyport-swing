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

import java.util.Calendar;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.infinities.skyport.ServiceProvider;
import com.infinities.skyport.cache.CachedServiceProvider;
import com.infinities.skyport.model.PoolSize;
import com.infinities.skyport.model.configuration.Configuration;
import com.infinities.skyport.service.ConfigurationHome;
import com.infinities.skyport.service.ConfigurationLifeCycleListener;
import com.infinities.skyport.service.DriverHome;
import com.infinities.skyport.testcase.IntegrationTest;

@Category(IntegrationTest.class)
public class MainFrameManualIT {

	protected Mockery context = new JUnit4Mockery() {

		{
			setThreadingPolicy(new Synchroniser());
		}
	};
	private DriverHome driverHome;
	private ConfigurationHome configurationHome;
	private SortedMap<String, Class<? extends ServiceProvider>> driverMap = new TreeMap<String, Class<? extends ServiceProvider>>();
	private MainFrame frame;
	private CachedServiceProvider serviceProvider;


	@Before
	public void setUp() throws Exception {
		serviceProvider = context.mock(CachedServiceProvider.class);
		driverHome = context.mock(DriverHome.class);
		configurationHome = context.mock(ConfigurationHome.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {
		driverMap = new TreeMap<String, Class<? extends ServiceProvider>>();
		driverMap.put("noExtraValue", NoExtraValueServiceProvider.class);
		driverMap.put("withExtraValue", WithExtraValueServiceProvider.class);
		final Configuration configuration = new Configuration();
		configuration.setCacheable(true);
		configuration.setAccount("account");
		configuration.setProviderClass("withExtraValue");
		configuration.setCloudName("cloudName");
		configuration.setEndpoint("endpoint");
		configuration.setRegionId("regionId");
		configuration.setProviderName("providerName");
		configuration.getProperties().put("MyKey_SHARED", "public_key");
		configuration.getProperties().put("MyKey_SECRET", "private_key");
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getDelay().setNumber(60);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getDelay().setUnit(TimeUnit.MINUTES);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getTimeout().setNumber(5);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().getTimeout().setUnit(TimeUnit.HOURS);
		configuration.getAdminConfiguration().getAccountConfiguration().getCreate().setPoolSize(PoolSize.MEDIUM);
		configuration.getShortPoolConfig().setCoreSize(1);
		configuration.getShortPoolConfig().setKeepAlive(2);
		configuration.getShortPoolConfig().setMaxSize(3);
		configuration.getShortPoolConfig().setQueueCapacity(4);
		configuration.getMediumPoolConfig().setCoreSize(5);
		configuration.getMediumPoolConfig().setKeepAlive(6);
		configuration.getMediumPoolConfig().setMaxSize(7);
		configuration.getMediumPoolConfig().setQueueCapacity(8);
		configuration.getLongPoolConfig().setCoreSize(9);
		configuration.getLongPoolConfig().setKeepAlive(10);
		configuration.getLongPoolConfig().setMaxSize(11);
		configuration.getLongPoolConfig().setQueueCapacity(12);
		configuration.setModifiedDate(Calendar.getInstance());
		configuration.setId("demo");
		context.checking(new Expectations() {

			{
				allowing(driverHome).findAll();
				will(returnValue(driverMap));

				allowing(driverHome).findByName("noExtraValue");
				will(returnValue(NoExtraValueServiceProvider.class));

				allowing(driverHome).findByName("withExtraValue");
				will(returnValue(WithExtraValueServiceProvider.class));
			}
		});
		context.checking(new Expectations() {

			{
				oneOf(configurationHome).addLifeCycleListener(with(any(ConfigurationLifeCycleListener.class)));

				allowing(configurationHome).findById("demo");
				will(returnValue(serviceProvider));
				
				allowing(configurationHome).findByName("cloudName");
				will(returnValue(serviceProvider));

				allowing(serviceProvider).getConfiguration();
				will(returnValue(configuration));
			}
		});
		frame = new MainFrame(driverHome, configurationHome, "protocol", "ip", "port");
		frame.activate();
		frame.setVisible(true);
		frame.persist(configuration);
		Thread.sleep(20000);
	}

}
