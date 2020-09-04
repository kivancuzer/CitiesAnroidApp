package com.example.citiesandroidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.CityHolder> {

    ArrayList<City> cityList;

    public CityRecyclerAdapter(ArrayList<City> cityList) {
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public CityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.city_item,parent,false);

        return new CityHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityHolder holder, int position) {
        holder.textView.setText(cityList.get(position).name);

    }

    /**
     * Getting how many items in the city list.
     *
     * @return City List Size
     */
    @Override
    public int getItemCount() {
        return cityList.size();
    }

    /**
     * This method updates the City List for a synchronous recyclerview
     * @param cityList city List which is a new city List after crud operations.
     */
    public void updateList (ArrayList<City> cityList){
        this.cityList.clear();
        this.cityList=cityList;
        this.notifyDataSetChanged();
    }

    class CityHolder extends RecyclerView.ViewHolder{

        TextView textView;

        /**
         * Hold a view for the RecyclerView.
         *
         * @param itemView View which will be used in the RecyclerView.
         */
        public CityHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.txtRecyclerViewCityName);

        }
    }

}
