package de.datatrain.cockpita.test.pages;

import com.pgssoft.espressodoppio.idlingresources.ViewIdlingResource;
import de.datatrain.cockpita.test.core.Credentials;
import de.datatrain.cockpita.test.core.UIElements;
import de.datatrain.cockpita.test.core.WizardDevice;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class ActivationPage {

    public ActivationPage() {
        ViewIdlingResource viewIdlingResource = (ViewIdlingResource) new ViewIdlingResource(
                withId(UIElements.ActivationPage.startButton)).register();
    }

    public void enterEmailAddress() {
        WizardDevice.fillInputField(UIElements.ActivationPage.emailText, Credentials.EMAIL_ADDRESS);
    }

    public void clickStart() {
        onView(withId(UIElements.ActivationPage.startButton)).perform(closeSoftKeyboard(), click());
    }
}
