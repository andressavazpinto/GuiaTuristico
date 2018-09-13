package adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tcc.guiaturistico.R;
import com.tcc.guiaturistico.activity.ChatActivity;

import java.util.List;

import model.Message;
import util.DBController;

public class ChatAdapter extends BaseAdapter {
    private final List<Message> messages;
    private final Activity activity;
    private DBController crud;

    public ChatAdapter(List<Message> messages, Activity activity, Context context) {
        this.messages = messages;
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
        View view = activity.getLayoutInflater().inflate(R.layout.balloon_left, parent, false);

        if(message.getIdUser() == crud.getUser().getIdUser())
            view = activity.getLayoutInflater().inflate(R.layout.balloon_right, parent, false);

        TextView textViewContent = view.findViewById(R.id.textViewContent);
        TextView textViewDataHora = view.findViewById(R.id.textViewDataHora);

        textViewContent.setText(message.getContent());
        textViewDataHora.setText(message.getDataHora());

        return view;
    }
}
