package kef.technology.jumianet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import kef.technology.jumianet.databinding.GridItemBinding;

public class GridItemAdapter extends ArrayAdapter<ListItem> {

    public GridItemAdapter(@NonNull Context context, int resource, @NonNull List<ListItem> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        GridItemBinding binding;

        if(convertView == null){
            binding = GridItemBinding.inflate(LayoutInflater.from(getContext()));
            convertView = binding.getRoot();
            convertView.setTag(binding);
        }
        else
            binding = (GridItemBinding) convertView.getTag();
        ListItem item = getItem(position);
        binding.gridTitle.setText(item.getTitle());
        binding.gridImage.setImageResource(item.getImageRes());
        return convertView;
    }
}
