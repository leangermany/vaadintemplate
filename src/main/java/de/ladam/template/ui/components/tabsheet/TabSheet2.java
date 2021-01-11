package de.ladam.template.ui.components.tabsheet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.SelectedChangeEvent;

/**
 * <p>
 * This TabSheet extends the default (Vaadin-8-)TabSheet by using reflection.
 * The Class<? extends Component> is mapped to the Tab. The goal is the lazy
 * load the instances on tab select.
 * </p>
 * 
 * <p>
 * On default this selected tab is saved in the <code>VaadinSession</code>, so
 * the tab is opened again after page refresh.
 * </p>
 * 
 * <p>
 * Call <code>getLayout()</code> to get the TabSheet.
 * </p>
 * 
 * @author lam
 *
 */
public class TabSheet2 {

	private Tabs tabBar = new Tabs(false);
	private VerticalLayout contentContainer = new VerticalLayout();
	private VerticalLayout wrapper = new VerticalLayout(tabBar, contentContainer);

	private final HashMap<Tab, Class<?>> mappedTabs = new HashMap<>();
	private final HashMap<String, Tab> tabKeys = new HashMap<>();
	private final HashMap<Class<?>, Component> instances = new HashMap<>();

	private Tab defaultTab = null;
	private boolean saveOpenedTabInSession = true;
	private static final UUID tabSheetUUID = UUID.randomUUID();

	public TabSheet2() {
		this(true);
	}

	public TabSheet2(boolean saveOpenedTab) {
		saveOpenedTabInSession = saveOpenedTab;
		init();
	}

	private void init() {

		tabBar.setWidthFull();
		contentContainer.setSizeFull();
		wrapper.setSizeFull();
		contentContainer.setPadding(false);

		tabBar.addSelectedChangeListener(onChange -> {

			Tab selectedTab = onChange.getSelectedTab();
			String tabKey = getTabKey(selectedTab);
			Class<?> cls = mappedTabs.get(selectedTab);
			Component instance = instances.get(cls);

			if (instance == null) {

				try {
					Constructor<?> constructor = cls.getDeclaredConstructor();
					instance = (Component) constructor.newInstance();
					instances.put(cls, instance);

				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}

			}

			if (instance != null) {
				getContentContainer().removeAll();
				getContentContainer().add(instance);
			}

			saveSelectedTab(tabKey);

		});

		selectSavedOrDefault();
	}

	public Tab addTab(String title, Class<? extends Component> contentClass, boolean closable) {
		HorizontalLayout tabLayout = new HorizontalLayout(new Text(title));
		tabLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		tabLayout.setSpacing(false);

		Tab tab = new Tab(tabLayout);

		if (closable) {
			Span close = new Span(VaadinIcon.CLOSE_SMALL.create());
			tabLayout.add(close);
			close.addClickListener(e -> removeTab(tab));
		}

		addTab(tab, contentClass);

		return tab;

	}

	public Tab addTab(String title, Class<? extends Component> contentClass) {
		Tab tab = new Tab(title);
		return addTab(tab, contentClass);
	}

	public Tab addTab(Tab tab, Class<? extends Component> contentClass) {

		String tabKey = tab.getLabel() + contentClass.getName();
		tabKeys.putIfAbsent(tabKey, tab);

		mappedTabs.put(tab, (Class<?>) contentClass);
		tabBar.add(tab);

		_setDefaultTab(tab);

		return tab;
	}

	public Tab addTab(Tab tab) {
		Objects.requireNonNull(tab, "added Tab is null");
		tabBar.add(tab);
		return tab;
	}

	public Component addComponent(Component component) {
		Objects.requireNonNull(component, "added Tab is null");
		tabBar.add(component);
		return component;
	}

	private void saveSelectedTab(String key) {
		if (saveOpenedTabInSession) {
			UI.getCurrent().getSession().setAttribute(tabSheetUUID.toString(), key);
//			CateringLogger.debug("TABSHEET2: saving key " + key);
		}
	}

	public void selectSavedOrDefault() {
		if (!saveOpenedTabInSession)
			return;

		Object attribute = UI.getCurrent().getSession().getAttribute(tabSheetUUID.toString());
		try {
//			CateringLogger.debug("TABSHEET2: getting key " + attribute);
			String tabKey = (String) attribute;
			Tab tab = tabKeys.getOrDefault(tabKey, defaultTab);
			if (tab != null) {
				selectTab(tab);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getTabKey(Tab tab) {
		for (Map.Entry<String, Tab> entry : tabKeys.entrySet()) {
			if (entry.getValue().equals(tab)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void removeTab(Tab tab) {
		tabBar.remove(tab);
		tabKeys.remove(getTabKey(tab));
		Class<?> cls = mappedTabs.get(tab);
		instances.remove(cls);
		mappedTabs.remove(tab);
	}

	public void selectTab(Tab tab) {
		tabBar.setSelectedTab(tab);
	}

	public Tab getSelectedTab() {
		return tabBar.getSelectedTab();
	}

	public void addSelectedChangeListener(ComponentEventListener<SelectedChangeEvent> listener) {
		tabBar.addSelectedChangeListener(listener);
	}

	/**
	 * <p>
	 * This sets the default tab that will be opened on init.
	 * </p>
	 * <p>
	 * On default the first added tab is the default tab.
	 * </p>
	 * 
	 * @param tab
	 */
	public void setDefaultTab(Tab tab) {
		this.defaultTab = tab;
	}

	/**
	 * called by addTab. Only settet on the first call.
	 * 
	 * @param tab
	 */
	private void _setDefaultTab(Tab tab) {
		if (defaultTab == null) {
			setDefaultTab(tab);
		}
	}

	public void selectDefaultTab() {
		if (defaultTab != null) {
			selectTab(defaultTab);
		}
	}

	public boolean isSaveOpenedTabInSession() {
		return saveOpenedTabInSession;
	}

	public void setSaveOpenedTabInSession(boolean saveOpenedTabInSession) {
		this.saveOpenedTabInSession = saveOpenedTabInSession;
	}

	public VerticalLayout getContentContainer() {
		return contentContainer;
	}

	public Tabs getTabBar() {
		return tabBar;
	}

	public VerticalLayout getLayout() {
		return wrapper;
	}

}
