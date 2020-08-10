package com.example.socialgood.sdk;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.example.socialgood.activities.PaypalTesterActivity;
import com.example.socialgood.models.Donation;
import com.example.socialgood.models.Fundraiser;
import com.example.socialgood.models.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;

public class PaypalClient{
    private static final String TAG = "paymentExample";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AQD5AKY6HJPNuOyuGC3BWsI7Rbeyc2QjkvXNxXYe" +
            "VZzsbfclJgsA0dHJXUtXjp3TC6e34JJuCj1B6hhR";

    public static final int REQUEST_CODE_PAYMENT = 1;
    public static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    public static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private double amount;

    public PaypalClient(){
        amount = 0;
    }


    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    public PayPalConfiguration config(){
        return config;
    }

    public void onBuyPressed(View pressed) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        //PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

    }


    public PayPalPayment getNewDonation(double amount) {
        this.amount = amount;
        return new PayPalPayment(new BigDecimal(amount), "USD", "Donation to Social Good",
                PayPalPayment.PAYMENT_INTENT_SALE);
    }

    public void savePayment(final boolean makePost){
        ParseQuery<Fundraiser> query = ParseQuery.getQuery(Fundraiser.class);
        query.include(Fundraiser.KEY_OWNER);
        query.include(Fundraiser.KEY_TITLE);
        query.include(Fundraiser.KEY_DESCRIPTION);
        query.include(Fundraiser.KEY_AMOUNT_RAISED);
        query.getFirstInBackground(new GetCallback<Fundraiser>() {
            @Override
            public void done(Fundraiser object, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error finding fundraiser", e);
                    return;
                }
                saveDonation(object, makePost);
            }
        });
    }

    private void saveDonation(final Fundraiser fundraiser, final boolean makePost){
        final Donation donation = new Donation(ParseUser.getCurrentUser(), fundraiser, amount);
        donation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error finding fundraiser", e);
                    return;
                }
                Log.i(TAG, "Saved Donation");
                fundraiser.addToFunds(amount);
                if(makePost)
                    saveDonationPost(donation);
            }
        });
    }

    private void saveDonationPost(Donation donation) {
        Post post = new Post();
        post.setUser(ParseUser.getCurrentUser());
        post.setDonation(donation);
        post.setType(Post.DONATION_TYPE);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error saving donation post", e);
                    return;
                }
                Log.i(TAG, "Saved Donation");
            }
        });
    }
}