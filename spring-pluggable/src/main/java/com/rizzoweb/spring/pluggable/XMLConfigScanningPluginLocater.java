package com.rizzoweb.spring.pluggable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;


/**
 * An implementation of {@link PluginLocater} that scans for XML configurations
 * (matching a specific pattern), locating all beans that implement a given
 * {@link Plugin} type and registering them with a {@link Pluggable} target. The
 * pattern is typically a classpath resource pattern, for example
 * "classpath*:/spring/MyPlugins-*.xml".
 *
 * @param <PluginType>
 *            The type of {@link Plugin}s to be located and registered.
 */
public class XMLConfigScanningPluginLocater<PluginType extends Plugin> extends PluginLocater<PluginType> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private String configsLocationPattern;


	public XMLConfigScanningPluginLocater(Class<PluginType> pluginType, Pluggable<PluginType> target, String configsLocationPattern) {
		super(pluginType, target);
		setConfigsLocationPattern(configsLocationPattern);
	}


	public XMLConfigScanningPluginLocater(Class<PluginType> pluginType, Pluggable<PluginType> target) {
		super(pluginType, target);
	}


	protected XMLConfigScanningPluginLocater(Class<PluginType> pluginType) {
		super(pluginType);
	}


	protected XMLConfigScanningPluginLocater(Class<PluginType> pluginType, String configsLocationPattern) {
		this(pluginType);
		setConfigsLocationPattern(configsLocationPattern);
	}


	public void setConfigsLocationPattern(String pattern) {
		this.configsLocationPattern = pattern;
	}


	@Override
	protected Iterable<PluginType> findPluginBeans(Class<PluginType> beanType) {
		Assert.hasLength(configsLocationPattern, "configsLocationPattern property must be set and non-empty");

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getApplicationContext().getClassLoader());
		Resource[] resources;
		try {
			resources = resolver.getResources(configsLocationPattern);
		} catch (IOException ex) {
			throw new BeanInitializationException("Error loading config resources for pattern " + configsLocationPattern, ex);
		}

		ArrayList<PluginType> beans = new ArrayList();
		for (Resource aResource : resources) {
			beans.addAll(getBeansFromConfigFile(beanType, aResource));
		}

		return beans;
	}


	protected Collection<PluginType> getBeansFromConfigFile(Class<PluginType> beanType, Resource configFile) {
		log.debug("Loading context config from {}", configFile);
		GenericApplicationContext tempContext = new GenericApplicationContext(getApplicationContext());
		tempContext.refresh();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(tempContext);
		reader.loadBeanDefinitions(configFile);
		return tempContext.getBeansOfType(beanType).values();
	}

}
