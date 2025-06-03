package com.example.gemini;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gemini.CustomClasses.BoldFormatter;
import com.example.gemini.Model.ChatDataClass;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    EditText etPrompt;
    MaterialButton btnSend;
    TextView title;

    RecyclerView rcvChat;
    ChatsAdapter adapter;
    List<ChatDataClass> chatTextList;
    CircularProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gptchat);

        title= findViewById(R.id.title);
        rcvChat = findViewById(R.id.rcvChat);
        chatTextList = new ArrayList<>();

        etPrompt = findViewById(R.id.et);
        btnSend = findViewById(R.id.send);
        progressIndicator = findViewById(R.id.progress_indicator);

        etPrompt.requestFocus();

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String currentDate = formatter.format(new Date());
        title.setText(Html.fromHtml("Chat Us (Gemini)<br><small><small><small>"+ currentDate + "</small></small></small>",Html.FROM_HTML_MODE_LEGACY));

        etPrompt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnSend.setEnabled(!TextUtils.isEmpty(charSequence.toString().trim()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        adapter = new ChatsAdapter(this, chatTextList);
        rcvChat.setAdapter(adapter);


        btnSend.setOnClickListener(view -> {
            Log.e("TAG", "onCreate: Send btn Clicked");
            buttonSendChat();
        });

    }

    private void buttonSendChat() {
        String promptText = etPrompt.getText().toString().trim();
        etPrompt.setText("");

        chatTextList.add(new ChatDataClass(promptText,false));
        adapter.notifyItemInserted(chatTextList.size()-1);
        rcvChat.scrollToPosition(chatTextList.size()-1);
        progressIndicator.setVisibility(View.VISIBLE);

        GetChatResponseText chatResponseText = new GetChatResponseText();

        chatResponseText.getResponse(promptText, new GetChatResponseText.ResponseCallback() {
            @Override public void onSuccess(@NonNull String response) {
                chatTextList.add(new ChatDataClass(response,true));
                adapter.notifyItemInserted(chatTextList.size()-1);
                rcvChat.scrollToPosition(chatTextList.size()-1);
                progressIndicator.setVisibility(View.GONE);
            }

            @Override public void onError(@NonNull String errorMessage) {
                Toast.makeText(ChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Chat Response Error : " + errorMessage );
                progressIndicator.setVisibility(View.GONE);
            }
        });
    }


    public static class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

        final Context context;
        List<ChatDataClass> chatTextList;


        public ChatsAdapter(Context context, List<ChatDataClass> chatTextList) {
            this.context = context;
            this.chatTextList = chatTextList;
        }

        @NonNull
        @Override
        public ChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
            return new ChatsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatsAdapter.ViewHolder holder, int position) {
            ChatDataClass item = chatTextList.get(position);
            CharSequence formattedText = BoldFormatter.formatBold(item.getText());
//            CharSequence formattedText = (item.getText());
            holder.tvChat.setText(formattedText);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.tvChat.getLayoutParams();
            if (item.isResponse()){
                holder.tvChat.setBackgroundResource(R.drawable.round_card_3sides);
                params.gravity = Gravity.START;
            }
            else {
                holder.tvChat.setBackgroundResource(R.drawable.round_card_3sides_usr_chat);
                params.gravity = Gravity.END;
            }
            holder.tvChat.setLayoutParams(params);
        }

        @Override
        public int getItemCount() {
            return chatTextList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvChat;
            FrameLayout base;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvChat = itemView.findViewById(R.id.text);
                base = itemView.findViewById(R.id.base);
            }
        }
    }
}