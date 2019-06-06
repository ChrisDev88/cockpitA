package de.datatrain.cockpita.logon

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import de.datatrain.cockpita.R
import de.datatrain.cockpita.app.cockpit.start.StartActivity
import de.datatrain.cockpita.app.ConfigurationData
import de.datatrain.cockpita.app.ErrorHandler
import de.datatrain.cockpita.app.ErrorMessage
import de.datatrain.cockpita.app.SAPWizardApplication
import de.datatrain.cockpita.service.SAPServiceManager
import com.sap.cloud.mobile.foundation.authentication.AppLifecycleCallbackHandler
import com.sap.cloud.mobile.foundation.common.EncryptionError
import com.sap.cloud.mobile.foundation.securestore.OpenFailureException
import com.sap.cloud.mobile.onboarding.launchscreen.LaunchScreenSettings
import com.sap.cloud.mobile.onboarding.passcode.EnterPasscodeActivity
import com.sap.cloud.mobile.onboarding.passcode.EnterPasscodeSettings
import com.sap.cloud.mobile.onboarding.passcode.SetPasscodeActivity
import com.sap.cloud.mobile.onboarding.passcode.SetPasscodeSettings
import com.sap.cloud.mobile.onboarding.utility.OnboardingType
import java.util.concurrent.Executors

class LogonActivity: AppCompatActivity() {

    private var isResuming = false
    private var isAwaitingResult = false

    private var sapServiceManager: SAPServiceManager? = null

    private var secureStoreManager: SecureStoreManager? = null

    private var clientPolicyManager: ClientPolicyManager? = null

    private var errorHandler: ErrorHandler? = null

    private var configurationData: ConfigurationData? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isAwaitingResult = false

        when(requestCode) {
            LAUNCH_SCREEN -> when(resultCode) {
                RESULT_OK -> setPasscode()
                CONTEXT_IGNORE_SECURITY -> startSimpleActivity()
                RESULT_CANCELED -> finish()
                else -> startLaunchScreen()
            }
            SET_PASSCODE -> when(resultCode) {
                RESULT_OK -> startSimpleActivity()
                RESULT_CANCELED -> startLaunchScreen()
            }
            ENTER_PASSCODE -> when(resultCode) {
                RESULT_OK -> startSimpleActivity()
                RESULT_CANCELED -> finishAffinity()
                SetPasscodeActivity.POLICY_CANCELLED -> {
                    (application as SAPWizardApplication).resetApplication(this)
                }
            }
            SIMPLE_ACTIVITY -> {}
            else -> {}
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_AWAITING_RESULT_KEY, isAwaitingResult)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isAwaitingResult = savedInstanceState?.getBoolean(IS_AWAITING_RESULT_KEY)?: false

        sapServiceManager = (application as SAPWizardApplication).sapServiceManager
        secureStoreManager = (application as SAPWizardApplication).secureStoreManager
        clientPolicyManager = (application as SAPWizardApplication).clientPolicyManager
        errorHandler = (application as SAPWizardApplication).errorHandler
        configurationData = (application as SAPWizardApplication).configurationData

        val bundle = intent.extras
        isResuming = bundle?.getBoolean(IS_RESUMING_KEY, false)?: false

        FingerprintActionHandlerImpl.setDisableOnCancel(false)
        setContentView(R.layout.activity_logon)
        if (isAwaitingResult) {
            return;
        }

        val isOnBoarded = (application as SAPWizardApplication).isOnboarded
        if (!isOnBoarded) {
            // create the store for application data (with default passcode)
            try {
                secureStoreManager?.openApplicationStore()
            } catch (e: EncryptionError) {
            } catch(e: OpenFailureException) {
            }
            startLaunchScreen()
        } else {
            // config data must be present
            when(configurationData?.isLoaded) {
                false -> logErrorAndResetApplication(
                    resources.getString(R.string.config_data_error_title),
                    resources.getString(R.string.config_data_corrupted_description)
                )
                else -> {
                    val isUserPasscode = secureStoreManager?.isUserPasscodeSet ?: false
                    when(isUserPasscode) {
                        true -> when(secureStoreManager?.isApplicationStoreOpen) {
                            true -> startSimpleActivity()
                            else -> enterPasscode()
                        }
                        else -> {
                            openApplicationStore()
                            val executorService = Executors.newSingleThreadExecutor()
                            executorService.submit {
                                val clientPolicy = clientPolicyManager?.getClientPolicy(true)
                                val isPolicyEnabled = clientPolicy?.isPasscodePolicyEnabled!!
                                secureStoreManager?.isPasscodePolicyEnabled = isPolicyEnabled
                                val isDefaultEnabled = clientPolicyManager?.getClientPolicy(false)?.passcodePolicy?.isSkipEnabled!!
                                if (isPolicyEnabled && !isDefaultEnabled) {
                                    this@LogonActivity.runOnUiThread {
                                        val activity = AppLifecycleCallbackHandler.getInstance().activity
                                        val alertBuilder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
                                        val res = this@LogonActivity.resources
                                        alertBuilder.setTitle(res.getString(R.string.passcode_required))
                                        alertBuilder.setMessage(res.getString(R.string.passcode_required_detail))
                                        alertBuilder.setPositiveButton(res.getString(R.string.ok), null)
                                        alertBuilder.setOnDismissListener {
                                            val intent = Intent(activity, SetPasscodeActivity::class.java)
                                            val setPasscodeSettings = SetPasscodeSettings()
                                            setPasscodeSettings.saveToIntent(intent)
                                            isAwaitingResult = true
                                            this@LogonActivity.startActivityForResult(intent, SET_PASSCODE)
                                        }
                                    }
                                }
                            }
                            executorService.shutdown()
                            startSimpleActivity()
                        }
                    }
                }
            }
        }
    }

    private fun logErrorAndResetApplication(errorTitle: String, errorDetails: String) {
        errorHandler?.sendErrorMessage(ErrorMessage(errorTitle, errorDetails))
        (application as SAPWizardApplication).resetApplication(this)
    }

    private fun setPasscode() {
        val executorService = Executors.newSingleThreadExecutor()
        executorService.submit {
            val clientPolicy = clientPolicyManager?.getClientPolicy(true)
            val passcodePolicy = clientPolicy?.passcodePolicy
            // isPolicyEnabled defaults to true, because the error message informing the user the
            // passcode policy couldn't be retrieved is shown on the set passcode screen.
            val isPolicyEnabled = if (passcodePolicy != null) clientPolicy.isPasscodePolicyEnabled!! else true
            secureStoreManager?.isPasscodePolicyEnabled = isPolicyEnabled

            if (isPolicyEnabled) {
                val i = Intent(this@LogonActivity, SetPasscodeActivity::class.java)
                val setPasscodeSettings = SetPasscodeSettings()
                setPasscodeSettings.skipButtonText = getString(R.string.skip_passcode)
                setPasscodeSettings.saveToIntent(i)
                isAwaitingResult = true
                startActivityForResult(i, SET_PASSCODE)
            } else {
                openApplicationStore()
                (application as SAPWizardApplication).isOnboarded = true
                startSimpleActivity()
            }
        }
        executorService.shutdown()
    }

    private fun enterPasscode() {
        // if retry limit is reached, then EnterPasscode screen is opened in disabled mode, i.e. only
        // reset is possible
        val currentRetryCount = secureStoreManager?.getWithPasscodePolicyStore { passcodePolicyStore ->
            passcodePolicyStore.getInt(ClientPolicyManager.KEY_RETRY_COUNT)
        }!!
        val retryLimit = clientPolicyManager?.getClientPolicy(false)?.passcodePolicy!!.retryLimit
        if (retryLimit <= currentRetryCount) {
            // only reset is allowed
            val enterPasscodeIntent = Intent(this, EnterPasscodeActivity::class.java)
            val enterPasscodeSettings = EnterPasscodeSettings()
            enterPasscodeSettings.isFinalDisabled = true
            enterPasscodeSettings.saveToIntent(enterPasscodeIntent)
            isAwaitingResult = true
            startActivityForResult(enterPasscodeIntent, ENTER_PASSCODE)
        } else {
            // client policy is refreshed now in UnlockActivity
            val unlockIntent = Intent(this, UnlockActivity::class.java)
            isAwaitingResult = true
            startActivityForResult(unlockIntent, ENTER_PASSCODE)
        }
    }


    private fun startLaunchScreen() {
        val welcome = Intent(this, com.sap.cloud.mobile.onboarding.launchscreen.LaunchScreenActivity::class.java)

        LaunchScreenSettings().apply {
            isDemoAvailable = false
            launchScreenHeadline = getString(R.string.welcome_screen_headline_label)
            welcomeScreenType = OnboardingType.STANDARD_ONBOARDING
            launchScreenTitles = arrayOf(getString(R.string.application_name))
            launchScreenImages = intArrayOf(R.drawable.ic_android_white_circle_24dp)
            launchScreenDescriptions = arrayOf(getString(R.string.welcome_screen_detail_label))
            launchScreenPrimaryButton = getString(R.string.welcome_screen_primary_button_label)
        }.saveToIntent(welcome)
        isAwaitingResult = true
        startActivityForResult(welcome, LAUNCH_SCREEN)
    }

    private fun startSimpleActivity() {
        sapServiceManager!!.openODataStore {
            val intent = Intent(this@LogonActivity, StartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivityForResult(intent, SIMPLE_ACTIVITY)
        } 
    }

    private fun openApplicationStore() {
        try {
            secureStoreManager?.openApplicationStore()
        } catch (e: EncryptionError) {
            val errorTitle = resources.getString(R.string.secure_store_error)
            val errorDetails = resources.getString(R.string.secure_store_open_default_error_detail)
            val errorMessage = ErrorMessage(errorTitle, errorDetails, e, false)
            errorHandler?.sendErrorMessage(errorMessage)
        } catch (e: OpenFailureException) {
            val errorTitle = resources.getString(R.string.secure_store_error)
            val errorDetails = resources.getString(R.string.secure_store_open_default_error_detail)
            val errorMessage = ErrorMessage(errorTitle, errorDetails, e, false)
            errorHandler?.sendErrorMessage(errorMessage)
        }
    }
    
    companion object {
        const val IS_RESUMING_KEY = "isResuming"
        val IS_AWAITING_RESULT_KEY = "isAwaitingResult"
        private const val LAUNCH_SCREEN = 100
        private const val SET_PASSCODE = 200
        private const val ENTER_PASSCODE = 300
        private const val SIMPLE_ACTIVITY = 500
    }
}
