package de.datatrain.cockpita.test.testcases.ui;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import de.datatrain.cockpita.logon.LogonActivity;
import de.datatrain.cockpita.test.core.BaseTest;
import de.datatrain.cockpita.test.core.Utils;
import de.datatrain.cockpita.test.pages.NoUIPage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NoUITests extends BaseTest {

    @Rule
    public ActivityTestRule<LogonActivity> activityTestRule = new ActivityTestRule<>(LogonActivity.class);

    @Test
    public void testNoUI() {

        // First do the onboarding flow
        Utils.doOnboarding();

        // Check NoUI screen
        NoUIPage noUIPage = new NoUIPage();

    }

}
