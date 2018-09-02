package adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tcc.guiaturistico.R;

import java.util.List;

import model.Message;

public class ChatAdapterRight extends BaseAdapter {
    private final List<Message> messages;
    private final Activity activity;

    public ChatAdapterRight(List<Message> messages, Activity activity) {
        this.messages = messages;
        this.activity = activity;
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
        View view = activity.getLayoutInflater().inflate(R.layout.balloon_right, parent, false);
        Message message = messages.get(position);

        TextView textViewContent = view.findViewById(R.id.textViewContent);
        TextView textViewDataHora = view.findViewById(R.id.textViewDataHora);

        textViewContent.setText(message.getContent());
        textViewDataHora.setText(message.getDataHora());

        return view;
    }
}
