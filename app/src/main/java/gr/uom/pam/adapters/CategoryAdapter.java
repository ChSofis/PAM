package gr.uom.pam.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gr.uom.pam.R;
import gr.uom.pam.model.Category;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> _categories = new ArrayList<>();
    private Category _selected;

    public CategoryAdapter(List<Category> Categorys) {
        _categories.addAll(Categorys);
    }

    public Category get_selected() {
        return _selected;
    }

    public void set_selected(Category category){
        item_clicked(category);
    }

    private Category get_item(int position) {
        return _categories.get(position);
    }

    private void item_clicked(Category Category) {
        int previous_selected = -1;
        if (_selected != null)
            previous_selected = _categories.indexOf(_selected);
        _selected = Category;
        if (previous_selected >= 0)
            notifyItemChanged(previous_selected);
        notifyItemChanged(_categories.indexOf(_selected));
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category Category = get_item(position);
        holder.bind_data(Category, Category.equals(_selected));
    }


    @Override
    public int getItemCount() {
        return _categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        Category _category;

        CategoryViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> CategoryAdapter.this.item_clicked(_category));
        }

        void bind_data(Category Category, boolean is_selected) {
            _category = Category;
            ((TextView) itemView).setText(Category.get_name());
            itemView.setActivated(is_selected);
        }
    }


}
