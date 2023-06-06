package com.example.askbento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView welcomeTextview;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextview =findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        messageList = new ArrayList<>();

        //setting up a recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        // to scroll from bottom to up
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        // clearing the edit text for the user before typing in their message

        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(b == true && messageEditText.getText().toString().compareTo("Say What?")==0){

                    messageEditText.setText("");
                }

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user question
                String message = messageEditText.getText().toString().trim();

                //determining if the user typed something before sending the message to the AI
                if(message.equals("") || message.equals(null)){
                    YoYo.with(Techniques.RubberBand).duration(1000).repeat(3).playOn(messageEditText);
                    Toast.makeText(MainActivity.this, "Please type in something first", Toast.LENGTH_LONG).show();

                    // Sending the message to the AI
                }else {
                    addToChat(message, Message.SENT_BY_ME);
                    callAPI(message);
                    messageEditText.setText("");
                    welcomeTextview.setVisibility(View.GONE);
                }
            }
        });


    }

    void addToChat(String message, String sentBy){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });

    }

    // function to display responses to the user
    void addResponse(String response){

        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    // function to send user message to the AI  for analysis and receiving the response
    void callAPI(String question){
        //initiating okhttp
        messageList.add(new Message("Searching....",Message.SENT_BY_BOT));

        //creating and sending user message to the ai
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","text-davinci-003");
            jsonBody.put("prompt",question);
            jsonBody.put("max_tokens",4000);// was 6000
            jsonBody.put("temperature",1); // was 0

            // below were not there before
            jsonBody.put("top_p",1);
            jsonBody.put("frequency_penalty",0.0);
            jsonBody.put("presence_penalty",0.0);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body =RequestBody.create(JSON,jsonBody.toString());//jsonBody.toString(),JSON)
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                addResponse("Sorry, i  have failed to load response  due to " + e.getMessage() +
                        "\n Please resend your message!!");

                Log.e("API Error: " ,e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                Log.e("API Response: " ,response.toString());
                //get ai response and display in to the user on successful response
                if(response.isSuccessful()){

                    JSONObject jsonBody = null;
                    try {

                        jsonBody = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonBody.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //get ai response and display in to the user on failed  response
                }else{

                    addResponse("Failed to load response  due to " + response.body().string() +
                            "\n Please resend your message!!"+ response.message());
                }

            }
        });

    }
}