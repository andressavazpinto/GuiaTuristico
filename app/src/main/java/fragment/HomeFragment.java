package fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tcc.guiaturistico.R;

import util.Message;

public class HomeFragment extends Fragment {

    public Button buttonRamdom, buttonByRegion;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_home, v, false);
        setupComponents(view);
        setRetainInstance(true); //preservar a instância do fragment
        return view;
    }

    public void setupComponents(View view) {
        buttonByRegion = view.findViewById(R.id.buttonByRegion);
        buttonRamdom = view.findViewById(R.id.buttonRamdom);
        buttonRamdom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRamdomly();
            }
        });
        buttonByRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByRegion();
            }
        });
    }

    private void searchByRegion() {
    }

    private void searchRamdomly() {
        ProgressDialog progress = new ProgressDialog(fragment.HomeFragment.this.getContext());
        progress.setMessage(Message.searchingGuide);
        progress.setIndeterminate(true);
        progress.show();


        //FoundGuideFragment foundGuideFragment = new FoundGuideFragment();
        //HomeActivity.setFragment(R.id.fragHome, foundGuideFragment);
        /*FragmentManager fm = getSupportFragmentManager();
        Fragment foundFragment = fm.findFragmentById(R.id.fragFound);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragHome, (Fragment) foundFragment);*/

        //Intent troca = new Intent(getActivity(), ListaTabelaPreco.class);
        //getActivity().startActivityForResult(troca, 3);
        //((HomeActivity)getActivity()).verifyStatus();
    }

}
