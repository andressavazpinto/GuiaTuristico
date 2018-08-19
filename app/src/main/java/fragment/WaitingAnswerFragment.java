package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tcc.guiaturistico.R;

public class WaitingAnswerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_waiting, v, false);
        //setupComponents(view);
        setRetainInstance(true); //preservar a instância do fragment
        return view;
    }
}
