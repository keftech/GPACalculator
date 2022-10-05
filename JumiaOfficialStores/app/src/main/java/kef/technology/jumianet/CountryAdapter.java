package kef.technology.jumianet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import kef.technology.jumianet.databinding.ListItemBinding;

public class CountryAdapter extends ArrayAdapter<ListItem> {

    int selectedPos = -1;

    public CountryAdapter(Context context, int layoutRes, List<ListItem> countryList){
        super(context, layoutRes, countryList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListItemBinding binding;

        if(convertView == null){
            binding = ListItemBinding.inflate(LayoutInflater.from(getContext()));
            convertView = binding.getRoot();
            convertView.setTag(binding);
        }
        else
            binding = (ListItemBinding) convertView.getTag();
        binding.radioBtn.setChecked(position == selectedPos);
        ListItem country = getItem(position);
        if(country != null){
            binding.radioBtn.setText(country.getTitle());
            binding.imageVw.setImageResource(country.getImageRes());
        }
        return convertView;
    }

    public void setSelectedPos(int selectedPos){
        this.selectedPos = selectedPos;
    }
}
