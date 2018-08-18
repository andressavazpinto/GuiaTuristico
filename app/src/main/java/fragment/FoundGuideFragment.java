package fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tcc.guiaturistico.R;

public class FoundGuideFragment extends Fragment {

    public Button buttonAccept, buttonReject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_found_guide, v, false);
        setupComponents(view);
        setRetainInstance(true); //preservar a instância do fragment
        return view;
    }

    public void setupComponents(View view) {
        buttonAccept = view.findViewById(R.id.buttonAccept);
        buttonReject = view.findViewById(R.id.buttonReject);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}
