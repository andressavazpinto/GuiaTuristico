package adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.tcc.guiaturistico.R;

import java.util.List;

import model.Message;
import util.DBController;

public class ChatAdapter extends BaseAdapter {
    private final List<Message> messages;
    private boolean translate;
    private final Activity activity;
    private DBController crud;

    public ChatAdapter(List<Message> messages, Boolean translate,  Activity activity, Context context) {
        this.messages = messages;
        this.translate = translate;
        this.activity = activity;
        crud = new DBController(context);
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
        if (message.getIdUser() == crud.getUser().getIdUser())
            view = activity.getLayoutInflater().inflate(R.layout.balloon_right, parent, false);
        else
            view = activity.getLayoutInflater().inflate(R.layout.balloon_left, parent, false);

        TextView textViewContent = view.findViewById(R.id.textViewContent);
        TextView textViewTranslation = view.findViewById(R.id.textViewTranslation);
        if(message.getType().equals("String")) {
            textViewContent.setText(message.getContent());
            if(translate & message.getTranslation() != null) {
                textViewTranslation.setVisibility(View.VISIBLE);
                //COLOCAR AQUI A TRADUÇÃO
                textViewTranslation.setText(message.getTranslation());
            }
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
