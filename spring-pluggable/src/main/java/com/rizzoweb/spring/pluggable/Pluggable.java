package com.rizzoweb.spring.pluggable;


/**
 * A Pluggable is a bean that can have {@link Plugin}s injected into it dynamically.
 *
 * @param <PluginType> The specific type of {@link Plugin} that this bean can accept
 */
public interface Pluggable<PluginType extends Plugin> {

	public void addPlugin(PluginType plugin);
}
