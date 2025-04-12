# Google Pay Integration Guide

This document explains how Google Pay is integrated into the CenPhone app and provides guidance for configuring it correctly for testing and production environments.

## Overview

The CenPhone app uses the Stripe Android SDK to handle payment processing, which includes Google Pay support. This allows users to pay using their Google Pay account if they have it set up on their device.

## Implementation Details

The Google Pay integration is handled through the following components:

1. **PaymentHelper.kt**: Contains the core logic for initializing and presenting the payment sheet with Google Pay options.
2. **PaymentScreen.kt**: Provides the UI for the payment selection and processing.
3. **AnimationUtils.kt**: Provides UI animations for payment buttons and elements.

## Google Pay Configuration

The Google Pay configuration is created in the `PaymentHelper.kt` file, in the `createGooglePayConfig()` method:

```kotlin
private fun createGooglePayConfig(): PaymentSheet.GooglePayConfiguration? {
    return try {
        PaymentSheet.GooglePayConfiguration(
            environment = PaymentSheet.GooglePayConfiguration.Environment.Test, // Use .Production for live environment
            countryCode = "CA", // Canada country code
            currencyCode = "CAD" // Canadian Dollar
        )
    } catch (Exception e) {
        Log.e("PaymentHelper", "Error creating Google Pay configuration: ${e.message}")
        null // Return null to gracefully fall back to regular payment methods
    }
}
```

### Configuration Parameters

- **environment**: Set to `Test` for development/testing, and `Production` for the live app.
- **countryCode**: Set to "CA" for Canada. Change this to match your target market (e.g., "US" for United States).
- **currencyCode**: Set to "CAD" for Canadian Dollar. Change this to match your target currency (e.g., "USD" for US Dollar).

## Error Handling

The implementation includes error handling to gracefully degrade if Google Pay is not available or if there are configuration issues:

1. The `createGooglePayConfig()` method is wrapped in a try-catch block to prevent crashes.
2. If Google Pay setup fails, the app falls back to standard payment methods.
3. Error messages are logged for debugging purposes.

## Moving to Production

Before releasing to production, make the following changes:

1. Change the Google Pay environment to `Production`:

```kotlin
environment = PaymentSheet.GooglePayConfiguration.Environment.Production
```

2. Make sure you have completed the Stripe merchant onboarding and verification.
3. Update your backend service to use the Stripe live API keys.
4. Test the integration thoroughly with both Google Pay and standard payment methods.

## Testing

To test Google Pay integration:

1. Run the app on a device with Google Pay set up (or an emulator with a Google account).
2. Navigate to the payment screen and select "Google Pay" as the payment method.
3. Complete the payment flow and verify that the transaction appears correctly in your Stripe dashboard.

## Troubleshooting

If Google Pay is not working:

1. Make sure the device has Google Pay installed and set up.
2. Check the logs for any error messages.
3. Verify that your Stripe account has Google Pay enabled.
4. Confirm the correct API keys are being used on both the client and server side.
5. Ensure the countryCode and currencyCode match your Stripe account settings.

## Additional Resources

- [Stripe Google Pay Documentation](https://stripe.com/docs/google-pay)
- [Google Pay API Documentation](https://developers.google.com/pay/api) 