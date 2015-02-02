package com.rizzoweb.spring.pluggable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * A bean that, upon initialization, automatically searches for beans
 * implementing a specific {@link Plugin} type and registers them with a
 * {@link Pluggable} target.
 *
 * @param <PluginType>
 *            The type of {@link Plugin}s to be located and registered.
 */
public abstract class PluginLocater<PluginType extends Plugin> implements ApplicationContextAware, InitializingBean {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ApplicationContext springContext;
	private Class<PluginType> pluginType;
	private Pluggable target;


	public PluginLocater(Class<PluginType> pluginType, Pluggable<PluginType> target) {
		this(pluginType);
		setTarget(target);
	}


	protected PluginLocater(Class<PluginType> pluginType) {
		this.pluginType = pluginType;
	}


	protected void setTarget(Pluggable aPluggable) {
		this.target = aPluggable;
	}


	@Override
	public void setApplicationContext(ApplicationContext context) {
		this.springContext = context;
	}


	protected ApplicationContext getApplicationContext() {
		return springContext;
	}


	@Override
	public void afterPropertiesSet() throws BeanInitializationException {
		Iterable<PluginType> beans = findPluginBeans(pluginType);
		for (PluginType aPlugin : beans) {
			log.debug("Registering plugin {}", aPlugin);
			registerWithTarget(aPlugin);
		}
	}


	protected abstract Iterable<PluginType> findPluginBeans(Class<PluginType> beanType) throws BeanInitializationException;


	protected void registerWithTarget(PluginType aPlugin) {
		if (target == null) {
			log.warn("No target to register plugins with");
		} else {
			target.addPlugin(aPlugin);
		}
	}

}
