package adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.google.common.base.Ascii;
import com.tcc.guiaturistico.R;

import java.util.ArrayList;
import java.util.List;

import model.Message;
import model.User;
import util.DBController;

public class ChatAdapter extends BaseAdapter {
    private final List<Message> messages;
    private boolean translate;
    private final Activity activity;
    private User u;

    public ChatAdapter(List<Message> messages, Boolean translate,  Activity activity, User u) {
        this.messages = messages;
        this.translate = translate;
        this.activity = activity;
        this.u = u;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messages.get(position);

        @SuppressLint("ViewHolder")
        View view;

        if (message.getIdUser() == u.getIdUser())
            view = activity.getLayoutInflater().inflate(R.layout.balloon_right, parent, false);
        else {
            view = activity.getLayoutInflater().inflate(R.layout.balloon_left, parent, false);
            TextView textViewTranslation = view.findViewById(R.id.textViewTranslation);

            if(translate & message.getTranslation() != null) {
                textViewTranslation.setVisibility(View.VISIBLE);

                String change[] = {"&#39;"};
                String changedTo = message.getTranslation();

                for(String n:change){
                   changedTo = message.getTranslation().replaceAll(n, "'");
                }

                textViewTranslation.setText(changedTo);
            }
        }

        TextView textViewContent = view.findViewById(R.id.textViewContent);

        if(message.getType().equals("String")) {
            textViewContent.setText(message.getContent());
        }
        else if(message.getType().equals("Image")) {
            textViewContent.setVisibility(View.GONE);
            ImageView imageContent = view.findViewById(R.id.imageContent);
            imageContent.setVisibility(View.VISIBLE);
            byte[] decodedString = Base64.decode(message.getContent(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageContent.setImageBitmap(decodedByte);
        }

        TextView textViewDataHora = view.findViewById(R.id.textViewDateTime);
        textViewDataHora.setText(DateFormat.format("dd/MM/yyyy (HH:mm)", message.getDateTime()));

        return view;
    }
}
