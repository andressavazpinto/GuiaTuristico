package adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.ConnectGuides;
import model.Search;
import model.SearchByRegion;
import model.SearchDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.ConnectGuidesService;
import service.SearchService;
import util.DBController;
import util.StatusConnectGuides;
import util.StatusSearch;

public class CountryCityAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CountryCityAdapter";
    private ArrayList<String> groupItem;
    private ArrayList<SearchByRegion> tempChild;
    private ArrayList<Object> Childtem;
    private LayoutInflater inflater;
    public Activity activity;
    private Search search;
    private ConnectGuides connectGuides;
    private DBController crud;

    public CountryCityAdapter(ArrayList<String> grList, ArrayList<Object> childItem, DBController crud) {
        this.crud = crud;
        search = new Search(0, null, crud.getUser().getIdUser());
        groupItem = grList;
        this.Childtem = childItem;
    }

    public void setInflater(LayoutInflater inflater, Activity activity) {
        this.inflater = inflater;
        this.activity = activity;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        tempChild = (ArrayList<SearchByRegion>) Childtem.get(groupPosition);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cities, null);
        }
        TextView textViewCity = convertView.findViewById(R.id.textViewCity);
        textViewCity.setText(tempChild.get(childPosition).getCity());

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        textViewName.setText("- " + tempChild.get(childPosition).getName());

        LinearLayout linearLayout = convertView.findViewById(R.id.linearScore);
        TextView textViewScore = linearLayout.findViewById(R.id.textViewScore);
        //ImageView star = linearLayout.findViewById(R.id.imageViewStar);
        String aux = " - " + tempChild.get(childPosition).getScoreS();
        double score = tempChild.get(childPosition).getScore();
        if(score != 0.00) {
            //star.setVisibility(View.VISIBLE);
            textViewScore.setText(aux);
        }
        else  {
            //star.setVisibility(View.GONE);
        }

        ImageView imageViewCircle = convertView.findViewById(R.id.imageCircle);
        if(tempChild.get(childPosition).getStatusSearch() != java.lang.Enum.valueOf(StatusSearch.class, "Searching"))
            imageViewCircle.setImageResource(R.drawable.ic_circle);
        else
            imageViewCircle.setImageResource(R.drawable.ic_circle_available);

        imageViewCircle.setVisibility(View.VISIBLE);

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tempChild.get(childPosition).getStatusSearch() == java.lang.Enum.valueOf(StatusSearch.class, "Searching")) {
                    ConnectGuides cg = new ConnectGuides(0, crud.getUser().getIdUser(), tempChild.get(childPosition).getIdUser(), Enum.valueOf(StatusConnectGuides.class, "Found"));
                    connectGuides(cg);
                }
                else {
                    Toast.makeText(activity, activity.getString(R.string.guideUnavailable), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ArrayList<String>) Childtem.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return groupItem.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.countries, null);
        }
        ((CheckedTextView) convertView).setText(groupItem.get(groupPosition));
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setStatus(Search search) {

        Gson g = new GsonBuilder().registerTypeAdapter(Search.class, new SearchDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SearchService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        SearchService service = retrofit.create(SearchService.class);

        Call<Void> requestSearch = service.update(search);

        requestSearch.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(activity, aux, Toast.LENGTH_LONG).show();
                }
                else if(response.isSuccessful()) {
                    Log.d(TAG, "Status alterado para searching");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
            }
        });
    }

    private void connectGuides(ConnectGuides cg) {
        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SearchService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        ConnectGuidesService service = retrofit.create(ConnectGuidesService.class);

        Call<ConnectGuides> requestSearch = service.connectGuides(cg);

        requestSearch.enqueue(new Callback<ConnectGuides>() {
            @Override
            public void onResponse(@NonNull Call<ConnectGuides> call, @NonNull Response<ConnectGuides> response) {
                if (response.isSuccessful()) {
                    connectGuides = response.body();

                    if (connectGuides != null) {
                        search.setStatus(Enum.valueOf(StatusSearch.class, "Found"));
                        setStatus(new Search(0, (Enum.valueOf(StatusSearch.class, "Found")), connectGuides.getIdUser1()));
                        setStatus(new Search(0, (Enum.valueOf(StatusSearch.class, "Found")), connectGuides.getIdUser2()));

                        activity.finish();

                        if (crud.getStatusSearch() != null) {
                            try {
                                crud.updateStatusSearch(search.getStatus().toString());
                            } catch (Exception e) {
                                Log.i(TAG, e.getMessage());
                            }
                        }

                        System.out.println("Resultado da busca: " + response.body());
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.noneGuide), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(activity, activity.getString(R.string.noneGuide), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConnectGuides> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(activity, aux, Toast.LENGTH_LONG).show();
            }
        });
    }
}