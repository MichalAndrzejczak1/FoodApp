package UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.Category;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Category> categoryList;
//    public CategoryorderRowBinding categoryBinding;

    private static ClickListener clickListener;

    public CategoryRecyclerAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public CategoryRecyclerAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category,viewGroup,false);

        return new ViewHolder(inflate);

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryRecyclerAdapter.ViewHolder viewHolder, int position) {
        viewHolder.categoryName.setText(categoryList.get(position).getTitle());
        switch (position){
            case 0: {
                Picasso.get()
                        .load(R.drawable.cat_1)
                        .placeholder(R.drawable.image_three)
                        .fit()
                        .into(viewHolder.categoryPic);
                viewHolder.mainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background1));
                break;
            }
            case 1: {
                Picasso.get()
                        .load(R.drawable.cat_2)
                        .placeholder(R.drawable.image_three)
                        .fit()
                        .into(viewHolder.categoryPic);
                viewHolder.mainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background2));
                break;
            }
            case 2: {
                Picasso.get()
                        .load(R.drawable.cat_3)
                        .placeholder(R.drawable.image_three)
                        .fit()
                        .into(viewHolder.categoryPic);
                viewHolder.mainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background3));
                break;
            }
            case 3: {
                Picasso.get()
                        .load(R.drawable.cat_4)
                        .placeholder(R.drawable.image_three)
                        .fit()
                        .into(viewHolder.categoryPic);
                viewHolder.mainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background4));
                break;
            }
            case 4: {
                Picasso.get()
                        .load(R.drawable.cat_5)
                        .placeholder(R.drawable.image_three)
                        .fit()
                        .into(viewHolder.categoryPic);
                viewHolder.mainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background5));
                break;
            }

        }


        Category category = categoryList.get(position);
        viewHolder.bind(category);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener {
        TextView categoryName;
        ImageView categoryPic;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.tvTitleProduct);
            categoryPic = itemView.findViewById(R.id.ivCategory);
            mainLayout = itemView.findViewById(R.id.mainLayout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Object obj) {
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        CategoryRecyclerAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

}
