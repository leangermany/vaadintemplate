package de.ladam.template.ui.inventory;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;

import de.ladam.template.util.i18n.TranslationKey;


public class InventoryView extends HorizontalLayout implements LocaleChangeObserver {

	private final Span placeholder = new Span("Placeholder");

	public InventoryView() {
		add(new Div(placeholder));
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
