package gr.uom.pam.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gr.uom.pam.R;
import gr.uom.pam.model.Store;


public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {
    List<Store> _stores = new ArrayList<>();
    private Store _selected;

    public StoreAdapter(List<Store> stores) {
        _stores.addAll(stores);
    }

    public Store get_selected() {
        return _selected;
    }

    private Store get_item(int position) {
        return _stores.get(position);
    }

    private void item_clicked(Store store) {
        int previous_selected = -1;
        if (_selected != null)
            previous_selected = _stores.indexOf(_selected);
        _selected = store;
        if (previous_selected >= 0)
            notifyItemChanged(previous_selected);
        notifyItemChanged(_stores.indexOf(_selected));
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, int position) {
        Store store = get_item(position);
        holder.bind_data(store, store.equals(_selected));
    }


    @Override
    public int getItemCount() {
        return _stores.size();
    }

    public void set_selected(Store store) {
        item_clicked(store);
    }

    class StoreViewHolder extends RecyclerView.ViewHolder {
        Store _store;

        StoreViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> StoreAdapter.this.item_clicked(_store));
        }

        void bind_data(Store store, boolean is_selected) {
            _store = store;
            ((TextView) itemView).setText(store.get_name());
            itemView.setActivated(is_selected);
        }
    }


}
