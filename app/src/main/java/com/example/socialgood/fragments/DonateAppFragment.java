package com.example.socialgood.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialgood.R;
import com.example.socialgood.sdk.PaypalClient;
import com.parse.ParseUser;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import static com.example.socialgood.sdk.PaypalClient.REQUEST_CODE_PAYMENT;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DonateAppFragment} factory method to
 * create an instance of this fragment.
 */
public class DonateAppFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = DonateAppFragment.class.getSimpleName();

    private EditText etDonation;
    private Button btnDonate;
    public PaypalClient paypalClient;
    public CheckBox checkBox;

    public DonateAppFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_donate_app, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Donate to App");

        etDonation = view.findViewById(R.id.etDonation);
        btnDonate = view.findViewById(R.id.btnDonate);
        checkBox = view.findViewById(R.id.cbDonate);
        checkBox.setChecked(true);
        paypalClient = new PaypalClient();

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double donation = Double.parseDouble(etDonation.getText().toString());
                makeDonation(donation);
            }
        });
    }

    private void makeDonation(double donation) {
        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */
        PayPalPayment donationPayment = paypalClient.getNewDonation(donation);

        Intent intent = new Intent(getContext(), PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalClient.config());

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, donationPayment);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        paypalClient.savePayment(checkBox.isChecked());
                        Toast.makeText(getContext(), "Donation was made successfully!", Toast.LENGTH_SHORT).show();
                        etDonation.setText("");
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */


                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    private void goProfileFragment(ParseUser user) {
        // Create new fragment and transaction
        Fragment newFragment = new ProfileFragment(user);
        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.frame_holder, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
}