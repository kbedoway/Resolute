package com.example.resoluteapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.resoluteapp.databinding.FragmentPrevActivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Table;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrevActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrevActivityFragment extends Fragment {

    private FragmentPrevActivityBinding binding;

    FirebaseFirestore DB = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPrevActivityBinding.inflate(inflater, container, false);

        //calls the fillTable() function when the page is created so it is filled simultaneously
        try {
            fillTable();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //function to fill the table with data from exercise collection
    public void fillTable() throws InterruptedException, ExecutionException {

        //get the username of the user that is currently logged in
        String currentUser = ((MainActivity)getActivity()).getUsername();

        //creates a reference to the database collection exercises
        CollectionReference exercisesRef = DB.collection("exercises_" + currentUser);

        //uses orderBy to sort the exercises by date and displays the most recent exercise at the top
        exercisesRef.orderBy("Date", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //checks that there is currently data to retrieve
                        if (!queryDocumentSnapshots.isEmpty()) {

                            //puts all the documents of data into a list
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            //finds the table in the fragment_prev_activity.xml
                            TableLayout tl = (TableLayout) getView().findViewById(R.id.tableLayout);
                            int columnWidth = tl.getWidth() / 4;

                            tl.removeViews(1, Math.max(0, tl.getChildCount() - 1));

                            //loops through all the documents in the list to fill the table
                            for (DocumentSnapshot d : list) {
                                //puts each piece of data in the document into its own variable
                                String date = d.getString("Date_String");
                                String exercise = d.getString("Exercise");
                                String amount = d.getString("Amount");
                                String unit = d.getString("Units");

                                //creates a new row for the exercise and 4 text views in the row
                                TableRow tr = new TableRow(getActivity().getApplicationContext());
                                TextView tv1 = new TextView(getActivity().getApplicationContext());
                                TextView tv2 = new TextView(getActivity().getApplicationContext());
                                TextView tv3 = new TextView(getActivity().getApplicationContext());
                                TextView tv4 = new TextView(getActivity().getApplicationContext());


                                //sets the first text view with the date data for the exercise
                                tv1.setText(date);
                                tv1.setTextColor(Color.BLACK);
                                tv1.setPadding(10, 10, 10, 10);
                                tv1.setTextSize(12);
                                tv1.setWidth(columnWidth);

                                //adds it to the table row in the first column
                                tr.addView(tv1, 0);

                                //sets the second text view with the exercise data for the exercise
                                tv2.setText(exercise);
                                tv2.setTextColor(Color.BLACK);
                                tv2.setPadding(10, 10, 10, 10);
                                tv2.setTextSize(12);
                                tv2.setWidth(columnWidth);

                                //adds it to the table row in the second column
                                tr.addView(tv2, 1);

                                //sets the third text view with the amount data for the exercise
                                tv3.setText(amount);
                                tv3.setTextColor(Color.BLACK);
                                tv3.setPadding(10, 10, 10, 10);
                                tv3.setTextSize(12);
                                tv3.setWidth(columnWidth);

                                //adds it to the table row in the third column
                                tr.addView(tv3, 2);

                                //sets the fourth text view with the unit data for the exercise
                                tv4.setText(unit);
                                tv4.setTextColor(Color.BLACK);
                                tv4.setPadding(10, 10, 10, 10);
                                tv4.setTextSize(12);
                                tv4.setWidth(columnWidth);

                                //adds it to the table row in the fourth column
                                tr.addView(tv4, 3);

                                //set navigation onClick listener to every exercise
                                tr.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((MainActivity)getActivity()).setExercise(d.getId());
                                        NavHostFragment.findNavController(PrevActivityFragment.this)
                                                .navigate(R.id.action_prevActivityFragment_to_replyFragment);
                                    }
                                });

                                //adds the row to the table
                                tl.addView(tr);
                            }
                        }
                        //Table succeeded, add navigation
                        buttonClicks();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Table failed, add navigation
                        buttonClicks();
                    }
                });
    }

    //Function that sets button's onClick() listeners only when it is convenient
    public void buttonClicks(){
        //Home Button
        binding.toHomeFromPreviousActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(PrevActivityFragment.this)
                        .navigate(R.id.action_prevActivityFragment_to_homeFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}