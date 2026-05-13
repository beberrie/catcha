package ph.edu.uscDCISMCatcha.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ph.edu.uscDCISMCatcha.data.models.ChatMessage;
import ph.edu.uscDCISMCatcha.data.models.EventModel;
import ph.edu.uscDCISMCatcha.data.repository.FirebaseRemoteDataSource;
import ph.edu.uscDCISMCatcha.databinding.FragmentChatbotBinding;
import ph.edu.uscDCISMCatcha.utils.Constants;

public class ChatBotFragment extends Fragment {

    private static final String TAG = "ChatBotFragment";
    private FragmentChatbotBinding binding;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private GenerativeModelFutures model;
    private FirebaseRemoteDataSource remoteDataSource;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private String eventsContext = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatbotBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        remoteDataSource = new FirebaseRemoteDataSource();
        initGemini();
        fetchEventsContext();

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        
        binding.rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvChat.setAdapter(adapter);

        // Initial greeting
        addBotMessage("Hello! I'm your Catcha assistant. I can help you find events and organizations. How can I help you today?");

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                addUserMessage(text);
                binding.etMessage.setText("");
                generateAIResponse(text);
            }
        });
    }

    private void initGemini() {
        GenerativeModel gm = new GenerativeModel(
                "gemini-flash-latest",
                Constants.GEMINI_API_KEY.trim()
        );
        model = GenerativeModelFutures.from(gm);
    }

    private void fetchEventsContext() {
        remoteDataSource.getAllEvents().addOnSuccessListener(queryDocumentSnapshots -> {
            StringBuilder context = new StringBuilder("Here are the available events at the university:\n");
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                EventModel event = doc.toObject(EventModel.class);
                if (event != null) {
                    context.append("- ").append(event.getTitle())
                            .append(" hosted by ").append(event.getOrgName())
                            .append(" at ").append(event.getLocation())
                            .append(". Description: ").append(event.getDescription())
                            .append("\n");
                }
            }
            eventsContext = context.toString();
            Log.d(TAG, "Events context fetched: " + eventsContext.length() + " chars");
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching events for context", e));
    }

    private void generateAIResponse(String userPrompt) {
        if (model == null) {
            Log.e(TAG, "Gemini model not initialized");
            addBotMessage("AI is not initialized. Please try again later.");
            return;
        }

        String fullPrompt = "System: You are Catcha Assistant, a helpful AI for a campus event management app called Catcha. " +
                "Your goal is to help students discover events and organizations. " +
                "Use the following event data to answer questions. If you don't know the answer, say you don't know.\n\n" +
                "Event Data:\n" + (eventsContext.isEmpty() ? "No events currently available." : eventsContext) + "\n\n" +
                "User: " + userPrompt;

        Content content = new Content.Builder()
                .addText(fullPrompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botResponse = result.getText();
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> addBotMessage(botResponse));
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e(TAG, "Gemini Error: " + t.getMessage(), t);
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        String rawError = t.getMessage() != null ? t.getMessage() : "Unknown Error";
                        String errorMsg = "Sorry, I'm having trouble connecting to the AI.";
                        
                        if (rawError.contains("404")) {
                            errorMsg += "\n\nError 404: The model name you used was not found. Please double check the name in initGemini().";
                        }
                        errorMsg += "\n\nDetails: " + rawError;

                        addBotMessage(errorMsg);
                    });
                }
            }
        }, executor);
    }

    private void addUserMessage(String text) {
        messages.add(new ChatMessage(text, true));
        adapter.notifyItemInserted(messages.size() - 1);
        binding.rvChat.scrollToPosition(messages.size() - 1);
    }

    private void addBotMessage(String text) {
        messages.add(new ChatMessage(text, false));
        adapter.notifyItemInserted(messages.size() - 1);
        binding.rvChat.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
