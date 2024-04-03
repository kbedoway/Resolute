package com.example.resoluteapp;

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

import com.example.resoluteapp.databinding.FragmentReplyBinding;
import com.example.resoluteapp.databinding.FragmentRequestBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class ReplyFragment extends Fragment {

    private FragmentReplyBinding binding;

    FirebaseFirestore DB = FirebaseFirestore.getInstance();

    String exerciseId;
    String spUsername;

    @Override
    public View onCreateView(
            @NonNull
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentReplyBinding.inflate(inflater, container, false);

        //get username and exercise's id for reply retrieving
        spUsername = ((MainActivity)getActivity()).getUsername();
        exerciseId = ((MainActivity)getActivity()).getExercise();

        //fill the tablelayout
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

        //set navigation functionality for back button
        binding.toPrevFromReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ReplyFragment.this)
                        .navigate(R.id.action_replyFragment_to_prevActivityFragment);
            }
        });
    }

    //function to fill the table with data from replies collection
    public void fillTable() throws InterruptedException, ExecutionException {
        //retrieve replies for current exercise
        DB.collection("exercises_" + spUsername)
                .document(exerciseId)
                .collection("replies")
                .orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //checks that there is currently data to retrieve
                        if (!queryDocumentSnapshots.isEmpty()) {
                            //puts all the documents of data into a list
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            //finds the table in the fragment_reply.xml
                            TableLayout tl = (TableLayout) getView().findViewById(R.id.reply_table_layout);
                            tl.removeViews(1, Math.max(0, tl.getChildCount() - 1));

                            //loops through all the documents in the list to fill the table
                            for (DocumentSnapshot d : list) {
                                String replyText = "";
                                replyText = replyText + d.getString("Username") +
                                            " says \"" + d.getString("Reply") + "\"";

                                //creates a new row for the reply, and a textview to put inside
                                TableRow tr = new TableRow(getActivity().getApplicationContext());
                                TextView tv = new TextView(getActivity().getApplicationContext());

                                //sets height of tablerow
                                tr.setMinimumHeight(60);

                                //sets text in tablerow to show replyText
                                tv.setText(replyText);

                                //add textview to tablerow
                                tr.addView(tv);

                                //add row to tablelayout
                                tl.addView(tr);
                            }
                        }
                        //else block takes place if query result is empty
                        else {
                            //finds the table in the fragment_reply.xml
                            TableLayout tl = (TableLayout) getView().findViewById(R.id.reply_table_layout);
                            tl.removeViews(1, Math.max(0, tl.getChildCount() - 1));

                            TableRow tr = new TableRow(getActivity().getApplicationContext());
                            TextView tv = new TextView(getActivity().getApplicationContext());

                            tv.setText("No replies yet...");
                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                            tr.addView(tv);
                            tl.addView(tr);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}