package de.ladam.template.components.elements;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.dom.Element;

@Tag("template-element") // Component needs to have a @link @Tag-Annotation
@CssImport("./styles/bubble-style.css")
public class TemplateElement extends Component implements HasStyle, HasComponents {

	private final Element timeElement = new Element("span");

	private final Element textElement = new Element("span");

	private final Button showMoreButton = new Button();

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
	private static final int maxLength = 1000;

	public TemplateElement(String content, LocalDateTime dateTime) {

		// interface @link HasComponents
		add(new H3("This is an self build Component"));

		String subText = setSubstring(content);

		textElement.setAttribute("part", "text");
		textElement.setText(subText);

		showMoreButton.getElement().setAttribute("part", "showmore");
		showMoreButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		showMoreButton.setText("Mehr anzeigen");
		showMoreButton.addClickListener(e -> {
			textElement.setText(content);
			showMoreButton.setVisible(false);
		});

		timeElement.setAttribute("part", "time");
		timeElement.setAttribute("class", "noSelect");
		timeElement.setText(dateTime.format(dateTimeFormatter));

		// https://vaadin.com/docs-beta/v14/flow/element-api/tutorial-shadow-root/
		// doesn't work in quick test
//		ShadowRoot shadowRoot = getElement().attachShadow();
//		shadowRoot.appendChild(textElement);

		getElement().appendChild(timeElement, textElement, showMoreButton.getElement());
		// has style interface
		addClassName("chatroombubble");

	}

	private String setSubstring(String text) {
		if (text.length() > maxLength) {
			showMoreButton.setVisible(true);
			return text.substring(0, maxLength) + "... ";
		}
		showMoreButton.setVisible(false);
		return text;
	}

	public void setText(String label) {
		this.textElement.setText(label);
	}

}
