package com.example.resoluteapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.resoluteapp.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    //Initialize the database object in fragment
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Create tag for Error reporting in Logcat
    private static final String TAG = "MainActivity";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //Disable Back Button built in to Android (Prevents back button after logout bug)
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing to prevent back button
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        //Check if user is already logged in, and navigate to HomeFragment if true
        if(((MainActivity)getActivity()).checkForUsername()){
            //Navigate to home screen
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_homeFragment);
        }

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Login Button
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Username and Password EditText items
                EditText usernameET = (EditText)getActivity().findViewById(R.id.login_username_entry);
                EditText passwordET = (EditText)getActivity().findViewById(R.id.login_password_entry);

                //Get entered username and password
                String usernameString = usernameET.getText().toString();
                String passwordString = passwordET.getText().toString();

                //Hash(encrypt) password
                String hashword = ((MainActivity)getActivity()).hashString(passwordString);

                //Search "users" for matching username
                db.collection("users")
                        .whereEqualTo("username", usernameString)
                        .whereEqualTo("password", hashword)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    //Only 1 user exists with entered username and password match
                                    if(task.getResult().size() == 1) {
                                        //Show Toast informing of valid username/password
                                        Toast toast = Toast.makeText(
                                                getActivity().getApplicationContext(), "Logging in", Toast.LENGTH_SHORT
                                        );
                                        toast.show();

                                        //Set username and password in SharedPreferences
                                        ((MainActivity)getActivity()).setUsernameAndPassword(
                                                usernameString, hashword
                                        );

                                        //Navigate to home screen
                                        NavHostFragment.findNavController(LoginFragment.this)
                                                .navigate(R.id.action_loginFragment_to_homeFragment);
                                    }
                                    //If there isn't exactly one match, login fails
                                    else{
                                        //Show Toast informing of invalid username/password
                                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        //Register Button
        binding.toRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}