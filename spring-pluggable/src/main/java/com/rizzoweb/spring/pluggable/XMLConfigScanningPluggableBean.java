package com.rizzoweb.spring.pluggable;

import java.util.ArrayList;
import java.util.Collection;


/**
 * An implementation of {@link Pluggable} that manages the collection of
 * plugins as well as acting as its own {@link PluginLocater}.
 *
 * @param <PluginType>
 *            The type of {@link Plugin}s that this bean locates and uses.
 */
public class XMLConfigScanningPluggableBean<PluginType extends Plugin>
				extends XMLConfigScanningPluginLocater<PluginType>
				implements Pluggable<PluginType> {

	private Collection<PluginType> plugins;


	public XMLConfigScanningPluggableBean(Class<PluginType> pluginType) {
		super(pluginType);
	}


	public XMLConfigScanningPluggableBean(Class<PluginType> pluginType, String configsLocationPattern) {
		super(pluginType, configsLocationPattern);
	}


	protected Collection<PluginType> getPlugins() {
		if (plugins == null) {
			plugins = new ArrayList<>();
		}

		return plugins;
	}


	@Override
	public void addPlugin(PluginType plugin) {
		getPlugins().add(plugin);
	}


	public void setPlugins(Collection<PluginType> plugins) {
		getPlugins().clear();
		getPlugins().addAll(plugins);
	}


	@Override
	protected void registerWithTarget(PluginType aPlugin) {
		// I am the target, simply add the plugin to myself
		addPlugin(aPlugin);
	}

}
