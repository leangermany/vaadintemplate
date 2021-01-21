package de.ladam.template.ui.inventory;

import java.io.InputStream;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;

import de.ladam.template.util.i18n.TranslationKey;

public class InventoryView extends FlexLayout implements LocaleChangeObserver {

	private final Span placeholder = new Span("Placeholder");

	private final static String htmlResource = "html/inventory.html";

	public InventoryView() {
		InputStream is = null;
		try {
			is = InventoryView.class.getClassLoader().getResourceAsStream(htmlResource);
		} catch (Exception e) {
			Notification.show("Error on class loader", 3000, Position.BOTTOM_END);
		}
		if (is == null) {
			removeAll();
			add(new Div(new Span("resouce not found")));
		} else {
			Html html = new Html(is);
			removeAll();
			add(html);
		}
		setSizeFull();
		setText();
	}

	private void setText() {
		placeholder.setText(TranslationKey.PLACEHOLDER_TEXT.getTranslation());
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		setText();
	}
}
