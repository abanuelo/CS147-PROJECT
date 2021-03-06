package com.bignerdranch.android.pife11;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Store extends AppCompatActivity {
    private TextView description, userCoins, itemDescription, shirtItemDescription;
    private TextView blueHatText, orangeHatText, pinkHatText, yellowHatText;
    private ImageView top, bottom, rewardType, leftArrowHat, rightArrowHat, leftArrowShirt, rightArrowShirt, hatsInDescription, shirtsInDescription;
    private String currentUserId, pifePoints, items;
    private int price, currXHat, currXShirt, currItem;
    private boolean result;
    private Handler handler;
    private TextView hatPrice;
    private ImageView hatItem, shirtItem;
    private Button buy, purchaseItem, saveOutfit;
    private DatabaseReference userDb;
    private ImageButton noHat, yellowHat, pinkHat, blueHat, orangeHat;
    private ImageButton noShirt, greenShirt, pinkShirt, yellowShirt, brownShirt;
    Pair currHatShirt = new Pair(0, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Stats").child("dressed");
        handler = new Handler();

        //Importing the side buttons for functionality of the store
        leftArrowHat = findViewById(R.id.left_arrow_hat);
        rightArrowHat = findViewById(R.id.right_arrow_hat);
        leftArrowShirt = findViewById(R.id.left_arrow_shirt);
        rightArrowShirt = findViewById(R.id.right_arrow_shirt);
        saveOutfit = findViewById(R.id.save_outfit);

        //Importing the hat items
        hatItem = findViewById(R.id.hat_item);
        hatPrice = findViewById(R.id.hat_price);
        shirtItem = findViewById(R.id.shirt_item);


        //Import the Hat Image in the Description Section of the Store
        //hatsInDescription = findViewById(R.id.hat_description);
        //shirtsInDescription = findViewById(R.id.shirt_description);
        itemDescription = findViewById(R.id.description);
        shirtItemDescription = findViewById(R.id.shirt_des);
        purchaseItem = findViewById(R.id.itemPurchaseButton);
        currXHat = 0;
        currXShirt = 0;
        currItem = 0;
        checkIfItemBought(); //autopopulates the view for the first time;
        checkFirstTutorialCompleted(); //autopopulates the view for the first time;


        //This will implement functionality when trying to iterate through the horizontal scroll view
        //As a test we will first be attempting to move around the horizontal scroll view via rightArrow for Hats
        rightArrowHat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currXHat == 4) {
                    currXHat = 0;
                } else {
                    currXHat += 1;
                }

                //This next part will implement the fact that after you click the right arrow to look at another item
                //Jemi's attire will chage
                changeJemisHat();

            }
        });

        //testing to return back to previous item using similar code to above
        leftArrowHat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currXHat == 0) {
                    currXHat = 4;
                } else {
                    currXHat -= 1;
                }

                changeJemisHat();
            }
        });

        //We will now begin the implementation of the right and left arrows for the shirt in our Store
        rightArrowShirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currXShirt == 4){
                    currXShirt = 0;
                } else {
                    currXShirt += 1;
                }

                changeJemisShirt();
            }
        });

        leftArrowShirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currXShirt == 0){
                    currXShirt = 4;
                } else{
                    currXShirt -= 1;
                }

                changeJemisShirt();
            }
        });


        //Current Coin Levels to Buy Objects
        userCoins = findViewById(R.id.user_coins);
        pifePoints = userCoins.getText().toString();


        //Find the Avatar Portions to Change
        top = findViewById(R.id.avatarTop);
        bottom = findViewById(R.id.avatarBottom);


        saveOutfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int shirtNum = 0;
                int hatNum = 0;

                //Sets the shirt number 0 - naked, 1- green, 2-pink, 3-yellow, 4-brown
                if (currXShirt == 0){
                    shirtNum = 1;
                } else if (currXShirt == 1){
                    shirtNum = 2;
                } else if (currXShirt == 2){
                    shirtNum = 3;
                } else if (currXShirt == 3){
                    shirtNum = 4;
                } else {
                    shirtNum = 0;
                }

                //Sets the hats number 0-naked, 1-blue, 2-orange, 3-pink, 4-yellow
                Log.d("currXHat", Integer.toString(currXHat));
                if(currXHat == 0){
                    hatNum = 1;
                } else if (currXHat == 1){
                    hatNum = 2;
                } else if (currXHat == 2){
                    hatNum = 3;
                } else if (currXHat == 3){
                    hatNum = 4;
                } else {
                    hatNum = 0;
                }
                saveOutfitInBackend(hatNum, shirtNum);
            }
        });


        //initializeFireBase();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationViewPerform);
        bottomNavigationView.setSelectedItemId(R.id.user_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                                                                     @Override
                                                                     public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                                                                         switch (menuItem.getItemId()){
                                                                             case R.id.friends_nav:
                                                                                 Intent collab = new Intent(Store.this, CollabHiFi2.class);
                                                                                 finish();
                                                                                 startActivity(collab);
                                                                                 break;
                                                                             case R.id.practice_nav:
                                                                                 Intent practice_intent = new Intent(Store.this, ChooseRoutineActivity.class);
                                                                                 finish();
                                                                                 startActivity(practice_intent);
                                                                                 break;
                                                                             case R.id.perform_nav:
                                                                                 Intent perform_intent = new Intent(Store.this, MyPerform.class);
                                                                                 finish();
                                                                                 startActivity(perform_intent);
                                                                                 break;
                                                                             case R.id.user_nav:
                                                                                 Intent profile_intent = new Intent(Store.this, Profile.class);
                                                                                 finish();
                                                                                 startActivity(profile_intent);
                                                                                 break;
                                                                         }
                                                                         return true;
                                                                     }
                                                                 }
        );

        getUserPifePoints();

        purchaseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int item_price = 1;
                if(currXHat == 0){
                    item_price = 5;
                } else if (currXHat == 1){
                    item_price = 10;
                } else if (currXHat == 2){
                    item_price = 15;
                } else if (currXHat == 3){
                    item_price = 20;
                } else {
                    item_price = 0;
                }
                if (Integer.parseInt(userCoins.getText().toString()) >= item_price && item_price!= 0){
                    //Places the item in the Backend For Future Use
                    DatabaseReference itemDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Store");
                    HashMap map = new HashMap();
                    if (item_price == 5){
                        map.put("BlueHat", true);
                        itemDescription.setText("");
                    } else if (item_price == 10){
                        map.put("OrangeHat", true);
                        itemDescription.setText("");
                    } else if (item_price == 15){
                        map.put("PinkHat", true);
                        itemDescription.setText("");
                    } else if (item_price == 20){
                        map.put("YellowHat", true);
                        itemDescription.setText("");
                    }
                    itemDb.updateChildren(map);

                    purchaseItem.setVisibility(View.INVISIBLE);
                    itemDescription.setText("Item purchased!");
                    changeUserPoints(item_price);
                    Toast.makeText(Store.this, "Item purchased!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Store.this, "Insufficient Funds! Practice More!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void changeUserPoints(final int item_price){
        final DatabaseReference itemDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Stats");
        itemDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot stat: dataSnapshot.getChildren()){
                    if (stat.getKey().equals("xp")){
                        long amount = (long) stat.getValue();
                        long new_amount = amount - item_price;
                        HashMap map = new HashMap();
                        map.put("xp", new_amount);
                        itemDb.updateChildren(map);
                        userCoins.setText(Long.toString(new_amount));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void checkIfItemBought(){
        final DatabaseReference itemDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Store");
        itemDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean updatedTextField = false;
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    String purchased_item = item.getKey();
                    items = purchased_item;
                    int item_index = getCurrItemIndex();

                    //Special case when you scroll to the view where Jemi has no shirt or hat
                    if(currItem == 4 && updatedTextField == false){
                        itemDescription.setText("Be naked!");
                        purchaseItem.setVisibility(View.INVISIBLE);
                        updatedTextField = true;
                    }

                    if (currItem == item_index && updatedTextField == false){
                        itemDescription.setText("Item purchased!");
                        purchaseItem.setVisibility(View.INVISIBLE);
                        updatedTextField = true;

                        //Clears the price off items that you already have purchased the item
                        if (currItem == 0){
                            hatPrice.setText("");
                        } else if (currItem == 1){
                            hatPrice.setText("");
                        } else if (currItem == 2){
                            hatPrice.setText("");
                        } else {
                            hatPrice.setText("");
                        }
                    }
                }
                if (updatedTextField == false){
                    itemDescription.setText("Buy item?");
                    purchaseItem.setVisibility(View.VISIBLE);
                    hatPrice.setVisibility(View.VISIBLE);

                    if (currItem == 0){
                        hatPrice.setText("$5");
                    } else if (currItem == 1){
                        hatPrice.setText("$10");
                    } else if (currItem == 2){
                        hatPrice.setText("$15");
                    } else if (currItem == 3){
                        hatPrice.setText("$20");
                    } else if (currItem == 4){
                        hatPrice.setText("");
                        purchaseItem.setVisibility(View.INVISIBLE);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //This method will change Jemis Shirt Across All parts of the screen
    private void changeJemisShirt(){
        if(currXShirt == 0){
            bottom.setImageResource(R.drawable.greenbottomtrans);
            //shirtItem.setImageResource(R.drawable.jemi_shirt_green);
            checkFirstTutorialCompleted();
        } else if (currXShirt == 1){
            bottom.setImageResource(R.drawable.pinkbottomtrans);
            //shirtItem.setImageResource(R.drawable.pinkshirttrans);
            checkSecondTutorialCompleted();
        } else if (currXShirt == 2){
            bottom.setImageResource(R.drawable.yellowbottomtrans);
            //shirtItem.setImageResource(R.drawable.yellowshirttrans);
            checkThirdTutorialCompleted();
        } else if (currXShirt == 3){
            bottom.setImageResource(R.drawable.brownbottomtrans);
            shirtItem.setImageResource(R.drawable.brownshirtlockedtrans);
            shirtItemDescription.setText("You must practice for 2 hours!");
        } else {
            bottom.setImageResource(R.drawable.undressedbottomtrans);
            shirtItem.setImageResource(R.drawable.none_icon);
            shirtItemDescription.setText("Be naked!");
        }
    }

    //This will change Jemis Hat Across All Parts of the Screen
    private void changeJemisHat(){
        if (currXHat == 0){
            top.setImageResource(R.drawable.bluetoptrans);
            hatItem.setImageResource(R.drawable.jemi_tiny_blue_hat);
            currItem = 0;
        } else if (currXHat == 1){
            top.setImageResource(R.drawable.orangetoptrans);
            hatItem.setImageResource(R.drawable.jemi_tiny_orange_hat);
            currItem = 1;
        } else if (currXHat == 2){
            top.setImageResource(R.drawable.pinktoptrans);
            hatItem.setImageResource(R.drawable.pinkhattrans);
            currItem = 2;
        } else if (currXHat == 3){
            top.setImageResource(R.drawable.yellowtoptrans);
            hatItem.setImageResource(R.drawable.yellowhattrans);
            currItem = 3;
        } else if (currXHat == 4){
            top.setImageResource(R.drawable.undressedtoptrans);
            hatItem.setImageResource(R.drawable.none_icon);
            hatPrice.setText("");
            currItem = 4;
        }
        checkIfItemBought();
    }

    private int getCurrItemIndex(){
        int item_index = 0;
        if (items.equals("BlueHat")){
            item_index = 0;
        } else if (items.equals("OrangeHat")){
           item_index = 1;
        } else if (items.equals("PinkHat")){
            item_index = 2;
        } else if (items.equals("YellowHat")){
           item_index = 3;
        } else {
            item_index = 4;
        }
        return item_index;
    }

    private void checkThirdTutorialCompleted(){
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Stats").child("TutorialThree");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    if (((Long) dataSnapshot.getValue()).intValue() >= 3){
                        shirtItemDescription.setText("Congrats! You have commented on a total of three videos!");
                        shirtItem.setImageResource(R.drawable.yellowshirttrans);
                        //shirtsInDescription.setImageResource(R.drawable.yellowshirttrans);
                    } else {
                        shirtItemDescription.setText("Comment on a total of three videos!");
                        shirtItem.setImageResource(R.drawable.yellowshirtlockedtrans);
                        //shirtsInDescription.setImageResource(R.drawable.yellowshirtlockedtrans);
                    }

                } else {
                    shirtItemDescription.setText("Comment on a total of three videos!");
                    shirtItem.setImageResource(R.drawable.yellowshirtlockedtrans);
                    //shirtsInDescription.setImageResource(R.drawable.yellowshirtlockedtrans);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkSecondTutorialCompleted(){
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Collaborations").child("Matches");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int num_friends = (int) dataSnapshot.getChildrenCount();
                if (num_friends >= 5){
                    shirtItemDescription.setText("Congrats! You have made at least 5 friends!");
                    shirtItem.setImageResource(R.drawable.pinkshirttrans);
                    //pinkShirt.setBackground(getDrawable(R.drawable.pinkshirttrans));
                    //shirtsInDescription.setImageResource(R.drawable.pinkshirttrans);
                } else {
                    shirtItemDescription.setText("Make a total of five friends!");
                    shirtItem.setImageResource(R.drawable.pinkshirtlockedtrans);
                    //pinkShirt.setBackground(getDrawable(R.drawable.pinkshirtlockedtrans));
                    //shirtsInDescription.setImageResource(R.drawable.pinkshirtlockedtrans);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFirstTutorialCompleted(){
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Stats").child("FirstTutorial");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    System.out.println("Some odd reason..." + dataSnapshot.getValue().toString());
                    //greenShirt.setBackground(getDrawable(R.drawable.jemi_shirt_green));
                    //shirtsInDescription.setImageResource(R.drawable.jemi_shirt_green);
                    shirtItemDescription.setText("Congrats! You completed one practice session!");
                    shirtItem.setImageResource(R.drawable.jemi_shirt_green);

                } else {
                    //greenShirt.setBackground(getDrawable(R.drawable.greenshirtlockedtrans));
                    shirtItemDescription.setText("Complete one practice session!");
                    shirtItem.setImageResource(R.drawable.greenshirtlockedtrans);
                    //shirtsInDescription.setImageResource(R.drawable.greenshirtlockedtrans);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserPifePoints() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Stats").child("xp");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    pifePoints = dataSnapshot.getValue().toString().trim();
                    userCoins.setText(pifePoints);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void saveOutfitInBackend(int hatNum, int shirtNum){
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference avatarDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("AvatarClothes");
        HashMap map = new HashMap();
        if (purchaseItem.getVisibility() != View.INVISIBLE) {
            Toast.makeText(this, "You have not purchased the hat! Outfit cannot be saved!", Toast.LENGTH_LONG).show();
        }else if (!shirtItemDescription.getText().toString().toLowerCase().contains("congrats") && !shirtItemDescription.getText().toString().equals("Be naked!")){
            Toast.makeText(this, "You have not unlocked this shirt! Outfit cannot be saved!", Toast.LENGTH_LONG).show();
        } else {
            map.put("hat", hatNum);
            map.put("shirt", shirtNum);
            avatarDb.updateChildren(map);
            Toast.makeText(this, "Outfit Saved", Toast.LENGTH_SHORT).show();
        }

    }

}



