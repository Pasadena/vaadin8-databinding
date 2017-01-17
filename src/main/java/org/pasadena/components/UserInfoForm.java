package org.pasadena.components;

import java.util.Arrays;

import com.vaadin.data.BeanBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import org.pasadena.models.City;
import org.pasadena.models.Country;
import org.pasadena.models.User;
import org.pasadena.models.User.Gender;
import org.pasadena.services.Backend;

public class UserInfoForm extends CustomComponent {

	private User user;
	private BeanBinder<User> dataBinder;

	private FormLayout formLayout;

	private TextField firstNameField;
	private TextField lastNameField;
	private TextField userNameField;
	private TextField emailField;
	private TextField ageField;
	private NativeSelect<Gender> genderSelectField;

	private TextField streetField;
	private TextField zipField;
	private ComboBox<Country> countryField;
	private ComboBox<City> cityField;

	private Backend backend;

	public UserInfoForm(final User user) {
		this.user = user;
		this.dataBinder = new BeanBinder<>(User.class);
		this.backend = new Backend();

		buildFormInputs();
		buildButtonRow();
		bindUserFields();
		bindAddressFields();

		dataBinder.readBean(user);

		setCompositionRoot(formLayout);
	}

	private void buildFormInputs() {
		this.formLayout = new FormLayout();

		this.firstNameField = new TextField("First name:");
		this.lastNameField = new TextField("Last name:");
		this.userNameField = new TextField("User name:");
		this.emailField = new TextField("Email:");
		this.ageField = new TextField("Age:");
		this.genderSelectField = new NativeSelect<>("Gender:", Arrays.asList(Gender.values()));

		this.streetField = new TextField("Street:");
		this.zipField = new TextField("Zip code:");
		this.countryField = new ComboBox<>("Country:", backend.getAvailableCountries());
		this.countryField.setItemCaptionGenerator((country) -> country.getName());
		this.countryField.addValueChangeListener(event -> this.cityField.setItems(this.backend.getCitiesForCountry(this.countryField.getValue())));
		this.cityField = new ComboBox<>("City:");
		this.cityField.setItemCaptionGenerator((city) -> city.getName());

		formLayout.addComponents(this.firstNameField, this.lastNameField, this.userNameField, this.emailField, this.ageField, this.genderSelectField);
		formLayout.addComponents(streetField, zipField, countryField, cityField);
	}

	private void buildButtonRow() {
		HorizontalLayout buttonRow = new HorizontalLayout();
		Button saveButton = new Button("Save", event -> this.saveUser());
		saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

		Button clearButton = new Button("Clear", event -> this.clearCurrentData());
		clearButton.addStyleName(ValoTheme.BUTTON_DANGER);

		buttonRow.addComponents(saveButton, clearButton);
		formLayout.addComponent(buttonRow);
	}

	private void bindUserFields() {
		dataBinder.forField(firstNameField)
				.withNullRepresentation("")
				.asRequired("First name is mandatory!")
				.bind(User::getFirstName, User::setFirstName);
		dataBinder.forField(lastNameField)
				.withNullRepresentation("")
				.asRequired("Last name is mandatory!")
				.bind(User::getLastName, User::setLastName);
		dataBinder.forField(userNameField)
				.asRequired("User name is mandatory!")
				.withValidator(new StringLengthValidator("Username must be at least three digits", 3, null))
				.bind(User::getUserName, User::setUserName);
		dataBinder.forField(emailField)
				.withNullRepresentation("")
				.asRequired("Email is mandatory!")
				.withValidator(new EmailValidator("Value is not a valid email address"))
				.bind(User::getEmail, User::setEmail);
		dataBinder.forField(ageField)
				.withNullRepresentation("")
				.withConverter(new StringToIntegerConverter("Age must be a valid number!"))
				.withValidator(RangeValidator.of("Age must be greater than zero", 0, null))
				.bind(User::getAge, User::setAge);
		dataBinder.forField(genderSelectField)
				.asRequired("Gender is mandatory!")
				.bind(User::getGender, User::setGender);
	}

	private void bindAddressFields() {
		dataBinder.forField(streetField)
				.withNullRepresentation("")
				.bind((usr) -> usr.getAddress().getStreet(), (usr, value) -> usr.getAddress().setStreet(value));
		dataBinder.forField(zipField)
				.withNullRepresentation("")
				.bind((usr) -> usr.getAddress().getZip(), (usr, value) -> usr.getAddress().setZip(value));
		dataBinder.forField(countryField)
				.bind((usr) -> usr.getAddress().getCountry(), (usr, value) -> usr.getAddress().setCountry(value));
		dataBinder.forField(cityField)
				.bind((usr) -> usr.getAddress().getCity(), (usr, value) -> usr.getAddress().setCity(value));
	}

	private void saveUser() {
		try {
			dataBinder.writeBean(this.user);
			Notification.show("Data saved!", this.user.toString(), Type.HUMANIZED_MESSAGE);
		} catch(ValidationException e) {
			handleValidationErrors(e);
		}
	}

	private void clearCurrentData() {
		dataBinder.readBean(this.user);
	}

	private void handleValidationErrors(ValidationException exception) {
		exception.getFieldValidationErrors().forEach(error -> {
			String errorMessage = error.getMessage().isPresent() ? error.getMessage().get() : "";
			Notification.show(errorMessage, Type.ERROR_MESSAGE);
		});
	}
}
