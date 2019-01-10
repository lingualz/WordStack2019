/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordstack;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2;
    private ViewGroup word1LinearLayout;
    private ViewGroup word2LinearLayout;
    private Stack<LetterTile> placedTiles = new Stack<LetterTile>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();

                if (word.length() == WORD_LENGTH) {
                    words.add(word);
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        word1LinearLayout = findViewById(R.id.word1);
       // word1LinearLayout.setOnDragListener(new TouchListener());
        word1LinearLayout.setOnDragListener(new DragListener());
        word2LinearLayout = findViewById(R.id.word2);
        //word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();

                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                /**
                 **
                 **  YOUR CODE GOES HERE
                 **
                 **/
                placedTiles.push(tile);
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    placedTiles.push(tile);
                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                    }
                    /**
                     **
                     **  YOUR CODE GOES HERE
                     **
                     **/
                    return true;
            }
            return false;
        }
    }

    public boolean onStartGame(View view) {
        word1LinearLayout.removeAllViews();
        word2LinearLayout.removeAllViews();
        stackedLayout.clear();
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");

        int index1 = random.nextInt(words.size());
        int index2 = random.nextInt(words.size());
        while (index2 == index1) {
            index2 = random.nextInt(words.size());
        }

        word1 = words.get(index1);
        word2 = words.get(index2);
        Log.i("test", "word1 is " + word1);
        Log.i("test", "word2 is " + word2);

        String msg = scrambleTwoWords();
        Log.i("test", "message is " + msg);


        for (int i = msg.length() - 1; i >= 0; i--) {
            stackedLayout.push(new LetterTile(this , msg.charAt(i)));
        }

//        messageBox.setText(msg);
        return true;
    }

    @NonNull
    private String scrambleTwoWords() {
        int counter1 = 0;
        int counter2 = 0;
        int isFirstWord = 0;
        String msg = "";

        while (counter1 < word1.length() && counter2 < word2.length()) {
            isFirstWord = random.nextInt(2);
            Log.i("test", "the random int this time is " + isFirstWord);

            // add from word1
            if (isFirstWord == 1) {
                msg = msg + word1.charAt(counter1);
                counter1++;
            }
            // add from word2
            else {
                msg = msg + word2.charAt(counter2);
                counter2++;
            }
        }

        if (counter1 == word1.length() ) {
            Log.i("test", "word 2 left " + word2.substring(counter2));
            msg = msg + word2.substring(counter2);
        }
        else if (counter2 == word2.length()) {
            Log.i("test", "word 1 left " + word1.substring(counter1));
            msg = msg + word1.substring(counter1);
        }

        return msg;
    }

    public boolean onUndo(View view) {
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        if (!placedTiles.empty()){
            placedTiles.pop().moveToViewGroup(stackedLayout);
        }


        return true;
    }
}
