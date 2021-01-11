package de.ladam.template.util.common;

import java.util.HashMap;
import java.util.Map;

import de.ladam.template.util.application.ApplicationLogger;

public class EventManager {

	private Map<String, Runnable> cateringEvents = new HashMap<>();

	/**
	 * Add an event to the <b>EventDispatcher</b>
	 * 
	 * @param name
	 * @param eventclass
	 */

	public EventManager addEvent(String name, Runnable eventclass) {
		cateringEvents.put(name, eventclass);

		if (cateringEvents.containsKey(name))
			ApplicationLogger.trace("Event " + name + " was already added. Overwritten!" + "EP001");

		return this;
	}

	/**
	 * This Method triggers a passed Event or a list of events if the passed
	 * parameter is a library name.
	 * 
	 * @param event or library name
	 */

	public void triggerEvent(String eventName) {
		if (cateringEvents.containsKey(eventName)) {
			cateringEvents.get(eventName).run();
		} else {
			ApplicationLogger.warn("Event unregistered" + eventName + "EP003");
		}
	}

}
