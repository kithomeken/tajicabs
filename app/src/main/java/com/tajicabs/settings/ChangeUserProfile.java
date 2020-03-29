package com.tajicabs.settings;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.tajicabs.R;
import com.tajicabs.database.AppDatabase;
import com.tajicabs.database.UserDetails;
import com.tajicabs.database.UserDetailsDao;
import com.tajicabs.global.Variables;

import static com.android.volley.VolleyLog.TAG;

public class ChangeUserProfile extends DialogFragment {
    static final String USER_PROFILE_DIALOG = "Update User Profile";

    private Context context;
    private static String dialogTitle;
    private static String dialogData;
    private static String updateCategory;

    static ChangeUserProfile newInstance(String title, String data, int category) {
        ChangeUserProfile fragment = new ChangeUserProfile();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("data", data);
        args.putString("category", String.valueOf(category));
        fragment.setArguments(args);

        return fragment;
    }

    public ChangeUserProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        dialogData = args.getString("data");
        dialogTitle = args.getString("title");
        updateCategory = args.getString("category");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.change_user_profile, null);

        final EditText editText = view.findViewById(R.id.changeData);

        if (dialogData != null) {
            editText.setText(dialogData);
            editText.setSelection(dialogData.length());
        }

        String positiveChange = "Update";
        String negativeChange = "Cancel";

        alertDialogBuilder.setView(view)
        .setTitle(dialogTitle)
        .setPositiveButton(positiveChange, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Variables.ACTIVITY_STATE == 0) {
                    Variables.ACTIVITY_STATE = 1;

                    ProgressDialog mProgressDialog;
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setMessage(getString(R.string.loading));
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();

                    updateRecords(editText.getText().toString(), updateCategory);

                    dialog.cancel();
                    mProgressDialog.dismiss();
                    Toast.makeText(context, "User Details Updated", Toast.LENGTH_SHORT).show();
                }
            }
        })
        .setNegativeButton(negativeChange, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                updateCategory = null;
                dialogData = null;
                dialogTitle = null;
            }
        });

        return alertDialogBuilder.create();
    }

    private void updateRecords(final String newData, String category) {
        if (TextUtils.isEmpty(newData)) {
            return;
        }

        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        final UserDetailsDao userDetailsDao = appDatabase.userDetailsDao();
        final UserDetails userDetails = userDetailsDao.getUserDetails();

        switch (category) {
            case "1":
                if (!userDetails.first_name.equalsIgnoreCase(newData)) {
                    userDetails.first_name = newData;
                    userDetailsDao.updateUserDetails(userDetails);
                }
            break;

            case "2":
                if (!userDetails.last_name.equalsIgnoreCase(newData)) {
                    userDetails.last_name = newData;
                    userDetailsDao.updateUserDetails(userDetails);
                }
            break;

            case "3":
                if (!userDetails.phone_number.equalsIgnoreCase(newData)) {
                    userDetails.phone_number = newData;
                    userDetailsDao.updateUserDetails(userDetails);
                }
            break;

            case "4":
                if (!userDetails.email.equalsIgnoreCase(newData)) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    firebaseUser.updateEmail(newData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email address updated.");

                                // Update Record
                                userDetails.email = newData;
                                userDetailsDao.updateUserDetails(userDetails);
                            } else {
                                String error = task.getException().getLocalizedMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            break;
        }
    }
}
